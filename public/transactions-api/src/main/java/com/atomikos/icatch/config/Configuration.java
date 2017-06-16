/**
 * Copyright (C) 2000-2017 Atomikos <info@atomikos.com>
 * <p>
 * LICENSE CONDITIONS
 * <p>
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.config;

import com.atomikos.datasource.RecoverableResource;
import com.atomikos.datasource.ResourceException;
import com.atomikos.icatch.CompositeTransactionManager;
import com.atomikos.icatch.RecoveryService;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TransactionService;
import com.atomikos.icatch.TransactionServicePlugin;
import com.atomikos.icatch.admin.LogAdministrator;
import com.atomikos.icatch.admin.LogControl;
import com.atomikos.icatch.provider.Assembler;
import com.atomikos.icatch.provider.ConfigProperties;
import com.atomikos.icatch.provider.TransactionServiceProvider;
import com.atomikos.recovery.RecoveryLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * Configuration is a facade for the transaction management core.
 * Allows the application code to find the transaction manager, even if
 * the actual implementation varies over time.
 */

@SuppressWarnings("all")
public final class Configuration
{
  private static CompositeTransactionManager ctxmgr_ = null;
  // the tm for the virtual machine instance

  private static Map resources_ = new HashMap();
  // filled on startup, contains all resources managed by the
  // transaction manager.

  private static List<RecoverableResource> resourceList_ = new ArrayList<>();
  // keep resources in a list, to enable ordered search of XAResource
  // this way, an AcceptAllXATransactionalResource can be added at the end

  private static List<LogAdministrator> logAdministrators_ = new ArrayList();
  // the registered log administrators

  private static RecoveryService recoveryService_;
  // needed for addResource to do recovery

  private static TransactionServiceProvider service_;
  // the transaction service for this VM.

  private static List<TransactionServicePlugin> tsListenersList_ = new ArrayList<>();

  private static List shutdownHooks_ = new ArrayList();

  private static Assembler assembler;

  private static ConfigProperties configProperties;


  private static void purgeResources()
  {
    for (RecoverableResource resource : getResources())
    {
      if (resource.isClosed())
        removeResource(resource.getName());
    }
  }

  /**
   * Construction not allowed.
   *
   */
  private Configuration()
  {
  }

  private static void addAllTransactionServicePluginServicesFromClasspath()
  {
    ServiceLoader<TransactionServicePlugin> loader = ServiceLoader.load(TransactionServicePlugin.class, Configuration.class.getClassLoader());
    for (TransactionServicePlugin l : loader)
    {
      registerTransactionServicePlugin(l);
    }
  }

  /**
   * Adds a shutdown hook to the configuration.
   * Shutdown hooks are managed here, since regular shutdown
   * of the transaction core should remove hooks
   * (cf case 21519).
   *
   * @param hook
   */
  private static synchronized void addShutdownHook(Thread hook)
  {
    if (shutdownHooks_.contains(hook)) return;

    shutdownHooks_.add(hook);

    try
    {
      Runtime.getRuntime().addShutdownHook(hook);
    }
    catch (IllegalStateException alreadyShuttingDownVm)
    {
      //ignore: this happens when the VM exits and this method
      //is called as part of one of the shutdown hooks executing
    }
  }


  /**
   * Removes all shutdown hooks from the system.
   * This method should be called on shutdown of the core.
   */

  private static synchronized void removeShutdownHooks()
  {
    Iterator it = shutdownHooks_.iterator();

    //first check if we are not already doing a VM exit;
    //don't remove the hooks if so
    boolean vmShutdown = false;
    while (it.hasNext())
    {
      Thread t = (Thread) it.next();
      if (t.equals(Thread.currentThread())) vmShutdown = true;
    }

    it = shutdownHooks_.iterator();
    while (!vmShutdown && it.hasNext())
    {
      Thread hook = (Thread) it.next();
      it.remove();
      try
      {
        Runtime.getRuntime().removeShutdownHook(hook);
      }
      catch (IllegalStateException alreadyShuttingDownVm)
      {
        //ignore: this happens when the VM exits and this method
        //is called as part of one of the shutdown hooks executing
      }
    }
  }

  /**
   * Retrieves the transaction service being used.
   *
   * @return TransactionService The transaction service.
   */

  public static TransactionService getTransactionService()
  {
    return service_;
  }

  /**
   * Add a transaction service listener.
   *
   * @param l
   *            The listener.
   */
  public static synchronized void registerTransactionServicePlugin(TransactionServicePlugin l)
  {
    if (service_ != null)
    {
      service_.addTSListener(l);
    }
    tsListenersList_.add(l);
  }

  /**
   * Remove a transaction service listener.
   *
   * @param l
   *            The listener.
   */
  public static synchronized void unregisterTransactionServicePlugin(TransactionServicePlugin l)
  {
    if (service_ != null)
    {
      service_.removeTSListener(l);
    }
    tsListenersList_.remove(l);
  }

  /**
   * Installs a composite transaction manager as a Singleton.
   *
   * @param compositeTransactionManager
   *            The instance to install.
   */

  public static synchronized void installCompositeTransactionManager(
    CompositeTransactionManager compositeTransactionManager)
  {

    ctxmgr_ = compositeTransactionManager;
  }

  /**
   * Get the composite transaction manager.
   *
   * @return CompositeTransactionManager The instance, or null if none.
   */

  public static CompositeTransactionManager getCompositeTransactionManager()
  {
    return ctxmgr_;
  }


  /**
   * Add a resource to the transaction manager domain. Should be called for
   * all resources that have to be recovered, BEFORE initializing the
   * transaction manager! The purpose of registering resources is mainly to be
   * able the recovery the ResourceTransaction context for each prepared
   * ResourceTransction. This is needed for those ResourceTransaction
   * instances that do not encapsulate the full state themselves, as in the
   * XAResource case.
   *
   * @param resource
   *            The resource to add.
   *
   * @exception IllegalStateException
   *                If the name of the resource is already in use.
   */

  public static synchronized void addResource(RecoverableResource resource)
    throws IllegalStateException
  {
    // ADDED with new recovery: temporary resources:
    // memory overflow can only happen upon addition of resources
    // so before each add, first purge closed resources to make room
    purgeResources();

    if (resources_.containsKey(resource.getName()))
      throw new IllegalStateException("Attempt to register second "
        + "resource with name " + resource.getName());


    //FIRST add init resource, only then add it - cf case 142795
    resource.setRecoveryService(recoveryService_);
    resources_.put(resource.getName(), resource);
    resourceList_.add(resource);
  }

  /**
   * Add a log administrator.
   *
   * @param admin
   */
  public static synchronized void addLogAdministrator(LogAdministrator admin)
  {
    if (logAdministrators_.contains(admin))
      return;

    logAdministrators_.add(admin);

    if (service_ != null)
    {
      admin.registerLogControl(service_.getLogControl());
    }
  }

  /**
   * Remove a log administrator.
   *
   * @param admin
   */
  public static void removeLogAdministrator(LogAdministrator admin)
  {
    logAdministrators_.remove(admin);

    if (service_ != null)
    {
      admin.deregisterLogControl(service_.getLogControl());
    }
  }

  /**
   * Get all registered logadministrators.
   *
   * @return List The logadministrators.
   */
  private static List<LogAdministrator> getLogAdministrators()
  {
    List<LogAdministrator> administrators = new ArrayList<>(logAdministrators_.size());

    Collections.copy(logAdministrators_, administrators);

    return administrators;
  }

  /**
   * Removes a resource from the config.
   *
   * @param name
   *            The resource's name.
   * @return RecoverableResource The removed object.
   */

  public static RecoverableResource removeResource(String name)
  {
    RecoverableResource recoverableResource = null;

    if (name != null)
    {
      recoverableResource = (RecoverableResource) resources_.remove(name);
      if (recoverableResource != null)
        resourceList_.remove(recoverableResource);
    }

    return recoverableResource;
  }

  /**
   * Get the resource with the given name.
   *
   * @return RecoverableResource The resource.
   * @param name
   *            The name to find.
   */

  public static RecoverableResource getResource(String name)
  {
    RecoverableResource res = null;
    if (name != null) res = (RecoverableResource) resources_.get(name);
    return res;
  }

  /**
   * Get all resources added so far, in the order that they were added.
   *
   * @return Enumeration The resources.
   */

  public static List<RecoverableResource> getResources()
  {
    // clone to avoid concurrency problems with
    // add/removeResource (new recovery makes this possible)
    List<RecoverableResource> resources = new ArrayList<>(resourceList_.size());
    Collections.copy(resourceList_, resources);
    return resources;
  }

  protected static synchronized Assembler getAssembler()
  {
    if (assembler == null) loadAssembler();
    return assembler;
  }

  private static void loadAssembler()
  {
    ServiceLoader<Assembler> loader = ServiceLoader.load(Assembler.class, Configuration.class.getClassLoader());
    Iterator<Assembler> it = loader.iterator();
    if (it.hasNext())
    {
      assembler = it.next();
    }
    else
    {
      throw new SysException("No Assembler service found - please make sure that the right jars are in your classpath");
    }
  }

  public static synchronized ConfigProperties getConfigProperties()
  {
    if (configProperties == null)
    {
      configProperties = getAssembler().initializeProperties();
    }

    return configProperties;
  }

  static synchronized void resetConfigProperties()
  {
    configProperties = null;
  }

  public static synchronized void shutdown(boolean force)
  {
    long maxWaitTime = 0;
    if (!force) maxWaitTime = Long.MAX_VALUE;
    shutdown(maxWaitTime);
  }

  public static synchronized void shutdown(long maxWaitTime)
  {
    if (service_ != null)
    {
      removeLogAdministrators(service_.getLogControl());
      service_.shutdown(maxWaitTime);
      notifyAfterShutdown();
      removeShutdownHooks();
      removeAndCloseResources(maxWaitTime <= 0);
      clearSystemComponents();
    }
  }

  private static void clearSystemComponents()
  {
    service_ = null;
    recoveryService_ = null;
    ctxmgr_ = null;
    tsListenersList_.clear();
    resetConfigProperties();
    assembler = null;
  }

  private static void notifyAfterShutdown()
  {
    for (TransactionServicePlugin p : tsListenersList_)
    {
      p.afterShutdown();
    }
  }

  private static void removeLogAdministrators(LogControl logControl)
  {
    for (LogAdministrator logAdministrator : Configuration.getLogAdministrators())
    {
      logAdministrator.deregisterLogControl(logControl);
      Configuration.removeLogAdministrator(logAdministrator);
    }
  }

  private static void removeAndCloseResources(boolean force)
  {
    for (RecoverableResource resource : Configuration.getResources())
    {
      Configuration.removeResource(resource.getName());

      try
      {
        resource.close();
      }
      catch (ResourceException re)
      {
        //Issue 10038:
        //Ignore errors in force mode: force is most likely
        //during VM exit; in that case interleaving of shutdown hooks
        //means that resource connectors may have closed already
        //by the time the TM hook runs. We don't want useless
        //reports in that case.
        //NOTE: any invalid states will be detected during the next
        //(re)init so they can be ignored here (if force mode)

        if (!force)
        {
          re.printStackTrace();
        }
      }
    }
  }

  /**
   *
   * @return False if already running.
   */
  public static synchronized boolean init()
  {
    boolean startupInitiated = false;

    if (service_ == null)
    {
      startupInitiated = true;
      addAllTransactionServicePluginServicesFromClasspath();
      ConfigProperties configProperties = getConfigProperties();
      notifyBeforeInit(configProperties);
      assembleSystemComponents(configProperties);
      initializeSystemComponents(configProperties);
      notifyAfterInit();
      if (configProperties.getForceShutdownOnVmExit())
      {
        addShutdownHook(new ForceShutdownHook());
      }
    }
    return startupInitiated;
  }

  private static void notifyAfterInit()
  {
    for (TransactionServicePlugin p : tsListenersList_)
    {
      p.afterInit();
    }
    for (LogAdministrator a : logAdministrators_)
    {
      a.registerLogControl(service_.getLogControl());
    }
    for (RecoverableResource r : resourceList_)
    {
      r.setRecoveryService(recoveryService_);
    }
  }

  private static void initializeSystemComponents(ConfigProperties configProperties)
  {
    service_.init(configProperties.getCompletedProperties());
  }

  private static void notifyBeforeInit(ConfigProperties configProperties)
  {
    for (TransactionServicePlugin p : tsListenersList_)
    {
      p.beforeInit(configProperties.getCompletedProperties());
    }
  }

  private static void assembleSystemComponents(ConfigProperties configProperties)
  {
    Assembler assembler = getAssembler();
    service_ = assembler.assembleTransactionService(configProperties);
    recoveryService_ = service_.getRecoveryService();
    ctxmgr_ = assembler.assembleCompositeTransactionManager();
  }

  public static RecoveryLog getRecoveryLog()
  {
    return recoveryService_.getRecoveryLog();
  }

  private static class ForceShutdownHook extends Thread
  {
    private ForceShutdownHook()
    {
      super();
    }

    public void run()
    {
      Configuration.shutdown(true);
    }
  }
}
