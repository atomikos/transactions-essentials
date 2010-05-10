package  com.atomikos.beans;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

 /**
  *
  *
  *An adapter class for property change events.
  */

class PropertyChangeAdapter
implements PropertyChangeListener
{
    private PropertyChangeSupport support_;
    //the listeners to notify with an adapted event
  
    private Editor source_;
    //the source for adapted events
    
    PropertyChangeAdapter ( Editor source  )
    {
        source_ = source;
        support_ = new PropertyChangeSupport ( source );
    } 
    
    public void propertyChange ( PropertyChangeEvent event )
   {
        PropertyChangeEvent adapted = new PropertyChangeEvent ( source_ , null , null , null );
        support_.firePropertyChange ( adapted );
        //System.err.println ( "PropertyChangeAdapter: propertyChange" );
   }
   
   public void addPropertyChangeListener ( PropertyChangeListener l )
   {
        support_.addPropertyChangeListener ( l );
   }
   
   public void removePropertyChangeListener ( PropertyChangeListener l )
   {
        support_.removePropertyChangeListener ( l );
   }
   
   
}
