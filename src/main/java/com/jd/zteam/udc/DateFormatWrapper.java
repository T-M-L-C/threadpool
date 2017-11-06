package com.jd.zteam.udc;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * 解决SimpleDateFormat线程不安全的缺陷
 *
 * @author liuhcao
 * @since 06/11/2017
 */
public class DateFormatWrapper {

    private static final int THREAD_NUMBER = 10;

    private static final ThreadLocal<SimpleDateFormat> sdf = ThreadLocal.withInitial(
            () -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    );

    public static String format(Date date) {
        return sdf.get().format(date);
    }

    public static Date parse(String str) throws ParseException {
        return sdf.get().parse(str);
    }

    @Test
    public void testSimpleDateFormat(){
        ExecutorService threadPool= Executors.newCachedThreadPool();//创建一个无大小限制的线程池

        List<Future<?>> futures=new ArrayList<>();

        for(int i=0;i<THREAD_NUMBER;i++){
            DateFormatTask task=new DateFormatTask();
            Future<?> future=threadPool.submit(task);//将任务提交到线程池

            futures.add(future);
        }

        for(Future<?> future : futures){
            try {
                future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    static class DateFormatTask implements Callable<Void>{

        @Override
        public Void call() throws Exception {
            String str=DateFormatWrapper.format(
                    DateFormatWrapper.parse("2017-11-11 00:00:00"));
            System.out.println(Thread.currentThread().getName()+"-> "+str);
            return null;
        }
    }

}