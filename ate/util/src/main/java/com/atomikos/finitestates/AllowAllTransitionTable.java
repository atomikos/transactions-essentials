package com.atomikos.finitestates;

/**
 *
 *
 *A default transition table implementation that allows any 
 *transition without really checking anything.
 */
 
 public class AllowAllTransitionTable implements TransitionTable
 {
 
      public AllowAllTransitionTable() {}
      
      /**
       *This method always returns true.
       */
       
      public boolean legalTransition ( Object from, Object to )
      {
          return true;	
      }	
 }
 
