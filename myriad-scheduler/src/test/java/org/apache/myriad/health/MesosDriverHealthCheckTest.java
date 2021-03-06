package org.apache.myriad.health;

import static org.junit.Assert.assertEquals;

import org.apache.myriad.scheduler.MockSchedulerDriver;
import org.apache.myriad.scheduler.MyriadDriver;
import org.apache.myriad.scheduler.MyriadDriverManager;
import org.junit.Test;

import com.codahale.metrics.health.HealthCheck.Result;

/**
 * Unit tests for MesosDriverHealthCheck
 */
public class MesosDriverHealthCheckTest {
  
  private static class HealthCheckTestTuple {
    MyriadDriverManager manager;
    MesosDriverHealthCheck checker;

    private HealthCheckTestTuple(MyriadDriverManager manager) {
      this.manager = manager;
      this.checker = new MesosDriverHealthCheck(manager);
    }

    public MyriadDriverManager getManager() {
      return manager;
    }

    public MesosDriverHealthCheck getChecker() {
      return checker;
    }
  }

  private HealthCheckTestTuple getTestStack() {
    MyriadDriver driver = new MyriadDriver(new MockSchedulerDriver());
    return new HealthCheckTestTuple(new MyriadDriverManager(driver));
  }

  @Test
  public void testCheckHealthyResult() throws Exception {
    HealthCheckTestTuple tuple = getTestStack();
    MyriadDriverManager manager = tuple.getManager();
    MesosDriverHealthCheck checker = tuple.getChecker();
    manager.startDriver(); 
    assertEquals(Result.healthy(), checker.check());
    manager.stopDriver(false);
  }

  @Test
  public void testCheckStoppedDriverUnhealthyResult() throws Exception {
    HealthCheckTestTuple tuple = getTestStack();
    MyriadDriverManager manager = tuple.getManager();
    MesosDriverHealthCheck checker = tuple.getChecker();
    manager.startDriver(); 
    manager.stopDriver(false);
    assertEquals(Result.unhealthy("Driver status: DRIVER_STOPPED"), checker.check());
  }

  @Test
  public void testCheckAbortedDriverUnhealthyResult() throws Exception {
    HealthCheckTestTuple tuple = getTestStack();
    MyriadDriverManager manager = tuple.getManager();
    MesosDriverHealthCheck checker = tuple.getChecker();
    manager.startDriver(); 
    manager.abortDriver();
    assertEquals(Result.unhealthy("Driver status: DRIVER_ABORTED"), checker.check());
  }
}