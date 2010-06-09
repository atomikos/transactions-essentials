package com.atomikos.icatch.imp;
import java.util.Stack;

/**
 *
 *
 *A test class for CompositeTransactionBase.
 */

public class TestCompositeTransactionBase extends AbstractCompositeTransaction
{

    public TestCompositeTransactionBase ( String tid , Stack lineage )
    {
        super ( tid , lineage , true);
    }


}
