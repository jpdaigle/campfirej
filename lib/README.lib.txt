JCampfire defines its own Ant task, a subclass of org.apache.tools.ant.Task, so we
must include ant.jar (from Apache Ant 1.6.5) in order to build JCampfire.

We don't need to include ant.jar when shipping jcampfire though, since JCampfire's ant
task will never get loaded unless you explicitly attempt to do so.

