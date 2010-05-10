//$Id: HeuristicParticipant.java,v 1.1.1.1 2006/08/29 10:01:06 guy Exp $
//$Log: HeuristicParticipant.java,v $
//Revision 1.1.1.1  2006/08/29 10:01:06  guy
//Import of 3.0 essentials edition.
//
//Revision 1.1.1.1  2006/04/29 08:55:39  guy
//Initial import.
//
//Revision 1.1.1.1  2006/03/29 13:21:33  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/23 16:25:28  guy
//Imported.
//
//Revision 1.1.1.1  2006/03/22 13:46:55  guy
//Import.
//
//Revision 1.1.1.1  2006/03/09 14:59:17  guy
//Imported 3.0 development into CVS repository.
//
//Revision 1.7  2005/08/05 15:04:21  guy
//Merged-in changes/additions of redesign-5-2004 (SOAP development branch).
//
//Revision 1.6  2004/10/12 13:03:51  guy
//Updated docs (changed Guy Pardon to Atomikos in many places).
//
//Revision 1.5  2004/03/22 15:38:06  guy
//Merged-in changes from branch redesign-4-2003.
//
//Revision 1.4.10.1  2003/06/20 16:31:47  guy
//*** empty log message ***
//
//Revision 1.4  2002/03/01 16:48:45  guy
//Added test facilities for testing heuristic error combinations
//during 2PC.
//
//Revision 1.3  2002/03/01 10:48:03  guy
//Updated to new prepare exception of HeurMixed.
//Added more failure modes and test facilities.
//
//Revision 1.2  2002/02/22 09:03:11  guy
//Adapted for ReleaseTester necessities.
//
//Revision 1.1  2002/02/12 21:19:45  guy
//Added generic test files.
//

package com.atomikos.icatch;

 /**
  *
  *
  *A heuristic participant: if included in the transaction, this 
  *participant will fail with a <b>heuristic exception</b> during 2PC.
  *
  */
  
public class HeuristicParticipant
extends AbstractParticipant
{
  
    
     /**
      *Flag value that indicates no failure should be done.
      */
      
    public static final int NO_FAIL = 0;
    
     /**
      *Flag value indicating the minimal failure flag value.
      */
    
    public static final int MIN_FAIL = NO_FAIL;
    
     /**
      *Flag value that indicates a heuristic hazard.
      */
      
    public static final int FAIL_HEUR_HAZARD = 1;
    
     /**
      *Flag value that indicates a heuristic mixed.
      */
    
    public static final int FAIL_HEUR_MIXED = 2;
    
    
     /**
      *Flag value to indicate a heuristic rollback.
      */
      
    public static final int FAIL_HEUR_ROLLBACK = 3;
    
    /**
      *Flag value to indicate a heuristic commit.
      */
      
    public static final int FAIL_HEUR_COMMIT = 4;

    
     /**
      *Flag indicating the max value of all failure flags.
      */
      
    public static final int MAX_FAIL = FAIL_HEUR_COMMIT;
    
    
     /**
      *Utility function to determine if a given list of 
      *integers contains at least one occurrence of the
      *given value.
      *@param list The list to look in.
      *@param value The value to look for.
      *@return boolean True iff list contains at least once
      *the given value.
      */
      
    private static final boolean contains ( int[] list , int value )
    {
        boolean ret = false;
        
        if ( list != null ) {
            int i = 0;
            while ( ! ret && i < list.length ) {
              if ( list[i] == value )
                  ret = true;
              i++;
            }
        }
        
        return ret;    
    }
    
     /**
      *Utility function to test if all values in a given
      *list equal a given value.
      *@param list The list of values.
      *@param value The value to check with.
      *@return boolean True iff <b>ALL</b> values in the list
      *equal the given value.
      */
      
    private static final boolean containsAll ( int[] list , int value )
    {
        boolean ret = true;
        
        if ( list != null ) {
            for ( int i = 0 ; i < list.length ; i++ ) {
                if ( list[i] != value )
                    ret = false;
            }
        }
        
        return ret; 
    }
    
     
      /**
       *Get the expected overall 2PC (failure) commit result for a
       *given combination of failure modes.
       *@param failCombinations An array of failure modes
       *where each is one of the predefined values. Should
       *NOT contain FAIL_HEUR_COMMIT.
       *@return int The expected overall result, expressed
       *as one of the predefined values.
       */
       
    public static int getExpectedCommitResult ( int[] failCombinations )
    throws Exception
    {
         int ret = NO_FAIL;
         
         //assert no heuristic commit
         if ( contains ( failCombinations , FAIL_HEUR_COMMIT ) )
            throw new Exception ( "Illegal value in array: FAIL_HEUR_COMMIT" );
            
         if ( contains ( failCombinations , FAIL_HEUR_MIXED ) ) {
              //at least one mixed case -> result is mixed as well
              ret = FAIL_HEUR_MIXED; 
         }
         else if ( containsAll ( failCombinations , FAIL_HEUR_ROLLBACK ) ) {
              //ALL are heuristic rb -> result as well
              ret = FAIL_HEUR_ROLLBACK;
         }
         else {
           
            //remaining possibilities are:
            //zero or more HR (but not ALL),
            //no HC
            //no HM
            //zero or more HH,
            //zero or more OK
            
            //remaining return values are:
            //NO_FAIL
            //FAIL_HEUR_HAZARD
            //FAIL_HEUR_MIXED
            
            if ( containsAll ( failCombinations , NO_FAIL ) ) {
                //NO_FAIL is ONLY possible if ALL are NO_FAIL
                ret = NO_FAIL;
            }
            else if ( contains ( failCombinations , FAIL_HEUR_HAZARD ) &&
                        ! contains ( failCombinations , FAIL_HEUR_COMMIT ) &&
                        ! contains ( failCombinations , FAIL_HEUR_ROLLBACK ) )
                        {
                //FAIL_HEUR_HAZARD is ONLY possible under this condition!
                ret = FAIL_HEUR_HAZARD;         
            }
            else {
                //all other cases MUST be heuristic mixed since
                //it is the only possible remaining value. 
                ret = FAIL_HEUR_MIXED;
            }
           
         }
         
         return ret;
    }    
    
    private int fail_;
    //the fail mode, one of the above constants.
    
     /**
      *Creates a new instance.
      *By default, this instance will fail with heuristic hazard
      *during commit or rollback.
      *@param msg The heuristic message to use.
      */
      
    public HeuristicParticipant ( HeuristicMessage msg )
    {
         super ( false, msg );
         fail_ = FAIL_HEUR_HAZARD;
    } 
  
    
     /** 
      *Test method to check if the next 2PC round will fail or not.
      *@return int One of the predefined failure modes.
      */
      
    public int getFailMode()
    {
        return fail_ ; 
    }
    
     /**
      *Set the failure mode for next 2PC.
      *Useful for simulating replay recovery.
      *@param fail One of the predefined failure modes.
      */
      
    public void setFailMode ( int fail )
    {
        fail_ = fail; 
    }
    
 

    public HeuristicMessage[] commit ( boolean onePhase )
        throws HeurRollbackException,
	     HeurHazardException,
	     HeurMixedException,
	     RollbackException,
	     SysException
    {   
        HeuristicMessage msg = getHeuristicMessages()[0];
        //System.err.println ( "Commit for: " + msg.toString() );
        switch ( fail_ ) {
            case FAIL_HEUR_MIXED: 
                    throw new HeurMixedException ( getHeuristicMessages() );
            case FAIL_HEUR_HAZARD:
                    throw new HeurHazardException ( getHeuristicMessages() );
            case FAIL_HEUR_ROLLBACK:
                    throw new HeurRollbackException ( getHeuristicMessages() );
            
            default:
                    break;
        }
        
        
        return super.commit ( onePhase );
    }
    
    
    public HeuristicMessage[] rollback()
        throws HeurCommitException,
	     HeurMixedException,
	     HeurHazardException,
	     SysException
    {
           
           switch ( fail_ ) {
            case FAIL_HEUR_MIXED: 
                    throw new HeurMixedException ( getHeuristicMessages() );
            case FAIL_HEUR_HAZARD:
                    throw new HeurHazardException ( getHeuristicMessages() );
            case FAIL_HEUR_COMMIT:
                    throw new HeurCommitException ( getHeuristicMessages() );
            
            default:
                    break;
        }
        
            
            return super.rollback();
    }
    
}
