package org.cloudera.htrace;

import java.io.File;
import java.io.IOException;

import org.cloudera.htrace.impl.LocalFileSpanReceiver;

public class Example {
  public static void main(String[] args) throws IOException, InterruptedException {
    String filename;
    if (args.length == 0) {
      filename = File.createTempFile("htrace-", ".txt").getPath();
    } else {
      filename = args[0];
    }
    LocalFileSpanReceiver receiver = new LocalFileSpanReceiver(filename);
    Trace.addReceiver(receiver);
    System.out.println("Writing to: " + filename);
    
    // Create a tree:
    // spanA (0-4)
    //   spanAA (0-1)
    //   spanAB (1-2)
    //     spanABA (1.5-2.5)
    //   spanAC (1-3)
    // t=0:
    Span spanA = Trace.startSpan("spanA", Sampler.ALWAYS);
    Span spanAA = Trace.startSpan("spanAA", spanA);
    Thread.sleep(1000);
    // t=1:
    spanAA.stop();
    Span spanAB = Trace.startSpan("spanAB", spanA);
    Span spanAC = Trace.startSpan("spanAC", spanA);
    Thread.sleep(500);
    // t=1.5:
    Span spanABA = Trace.startSpan("spanABA", spanAB);
    // t=2:
    Thread.sleep(500);
    spanAB.stop();
    spanABA.addAnnotation("foo".getBytes("UTF-8"), "bar".getBytes("UTF-8"));
    Thread.sleep(500);
    // t=2.5:
    spanABA.stop();
    Thread.sleep(500);
    // t=3:
    spanAC.stop();
    Thread.sleep(1000);
    // t=4:
    spanA.stop();
    
    receiver.close();
  }

}
