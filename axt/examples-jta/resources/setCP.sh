if [ -n "$JTA_HOME" ]
   then echo JTA_HOME="$JTA_HOME" 
   else 
        echo "Environment variable JTA_HOME is not set -- making a guess..."
        JTA_HOME=`pwd`
fi
CLASSPATH=.
for i in `ls  $JTA_HOME/lib/*.jar`
    do
        CLASSPATH=$CLASSPATH:$i
    done

for i in `ls $JTA_HOME/dist/*.jar`
    do 
	CLASSPATH=$CLASSPATH:$i
    done
    
for i in `ls  $JTA_HOME/examples/lib/*.jar`
    do
        CLASSPATH=$CLASSPATH:$i
    done    
CLASSPATH=$CLASSPATH:$JTA_HOME/examples/j2se:$JTA_HOME/examples/spring

echo
