package com.MsoftTexas.WeatherOnMyTripRoute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class test {

   static class kamlesh{
        int a=0;
        int b=0;
        int c=0;

        public kamlesh(int a,int b,int c) {
            this.a=a;
            this.b=b;
            this.c=c;
        }

       @Override
       public String toString() {
           return "a :"+a+" b :"+b+" c :"+c;
       }
   }

    public static void main(String... args){
        List<kamlesh> list=new ArrayList<>();

        list.add(new kamlesh(3,4,5));
        list.add(new kamlesh(2,3,7));
        list.add(new kamlesh(6,4,3));


        System.out.println(list.size());

        kamlesh [] k=new kamlesh[list.size()];

        System.out.println(Arrays.toString(list.toArray(new kamlesh[list.size()])));
    }
}
