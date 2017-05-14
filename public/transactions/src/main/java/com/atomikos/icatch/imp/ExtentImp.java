/**
 * Copyright (C) 2000-2016 Atomikos <info@atomikos.com>
 * <p>
 * LICENSE CONDITIONS
 * <p>
 * See http://www.atomikos.com/Main/WhichLicenseApplies for details.
 */

package com.atomikos.icatch.imp;

import com.atomikos.icatch.Extent;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.SysException;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 * The extent carries the information about the 'size' of a propagation after it
 * returns: the indirectly invoked servers, and the orphan detection information
 * for those.
 */

public class ExtentImp implements Extent
{
  private static final long serialVersionUID = -1010453448007350422L;

  private Map<String, Integer> participants = null;
  private boolean queried_ = false;

  // ToDo: can this instance variable  be removed -- IDEA says that it is written but never read
  private Deque<Participant> directs_;

  @SuppressWarnings("WeakerAccess")
  public ExtentImp()
  {
    participants = new HashMap<>();
    directs_ = new ArrayDeque<>();
  }

  @SuppressWarnings("unused")
  public ExtentImp(Map<String, Integer> map, Deque<Participant> directs)
  {
    participants = new HashMap<>(map);
    directs_ = new ArrayDeque<>(directs);
  }

  @SuppressWarnings("WeakerAccess")
  public void addRemoteParticipants(Map<String, Integer> participants)
    throws IllegalStateException, SysException
  {
    if (participants == null)
      return;
    Set<String> keySet = participants.keySet();
    for (String participantKey : keySet)
    {
      Integer count = this.participants.get(participantKey);
      if (count == null)
        count = 0;

      Integer cnt = participants.get(participantKey);
      count = count + cnt;

      this.participants.put(participantKey, count);
      // NOTE: this will replace the old participant, and if
      // it is a proxy then the buffered heuristic msgs will
      // also be replaced. This loses info if multiple PARALLEL calls
      // went to the same FIRST-ORDER server (i.e., directly invoked).
      // Never mind, though: it is considered bad practice
      // to execute parallel calls if they might act on the same
      // data. This is the case if they go to the same directly
      // invoked server.
    }
  }

  /**
   * @see Extent
   */

  public Map<String, Integer> getRemoteParticipants()
  {
    queried_ = true;
    return new HashMap<>(participants);
  }

  /**
   * @see Extent
   */
  @SuppressWarnings("unchecked")
  public Deque<Participant> getParticipants()
  {
    queried_ = true;
    return ((ArrayDeque<Participant>) directs_).clone();
  }

  /**
   * @see Extent
   */

  public synchronized void add(Participant participant, int count)
    throws SysException, IllegalStateException
  {
    Map<String, Integer> table = new HashMap<>();
    table.put(participant.getURI(), count);
    addRemoteParticipants(table);
    directs_.push(participant);
  }

  /**
   * @see Extent
   */

  public synchronized void add(Extent extent)
    throws IllegalStateException, SysException
  {
    if (queried_)
      throw new IllegalStateException("Adding extent no longer allowed");

    addRemoteParticipants(extent.getRemoteParticipants());

    for (Participant participant : extent.getParticipants())
    {
      directs_.push(participant);
    }
  }
}
