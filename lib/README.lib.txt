CampfireJ defines its own Ant task, a subclass of org.apache.tools.ant.Task, so we
must include ant.jar (from Apache Ant 1.6.5) in order to build CampfireJ.

We don't need to include ant.jar when shipping CampfireJ though, since CampfireJ's ant
task will never get loaded unless you explicitly attempt to do so.

