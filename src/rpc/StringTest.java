package rpc;

import static org.junit.Assert.*;

import org.junit.Test;

public class StringTest {
 @Test
 public void testSubString(){
   String str = new String("unit");
   assertEquals("unit", str.substring(1, 4));
 }	
}
