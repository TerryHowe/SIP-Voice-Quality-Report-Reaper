package com.tt.reaper.message;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

public class MessageQueue
{
   private static Logger logger = Logger.getLogger(MessageQueue.class);
   protected List<Message> queue;
   
   public MessageQueue()
   {
      queue = Collections.synchronizedList(new LinkedList<Message>());
   }

   public Message getBlocking()
   {
      try 
      {
         synchronized (queue)
         {
            while (queue.isEmpty())
               queue.wait();
            return (Message) queue.remove(0);
         }
      }
      catch (Exception e) {
    	  logger.error("Error with queue: ", e);
      }
      return null;
   }
   
   public Message get()
   {
      try 
      {
         return (Message) queue.remove(0);
      }
      catch (Exception e) {}
      return null;
   }

   public void add(Message e)
   {
      synchronized (queue)
      {
         queue.add(e);
         queue.notify();
      }
   }
}