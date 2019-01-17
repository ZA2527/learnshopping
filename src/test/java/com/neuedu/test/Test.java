package com.neuedu.test;

import com.neuedu.utils.BigDecimalUtils;

import java.math.BigDecimal;

public class Test{

    public static void main(String[] args) {
        /*System.out.println(0.05+0.01);
        BigDecimal bigDecimal = new BigDecimal("0.04");
        BigDecimal bigDecimal1 = new BigDecimal("0.01");*/
        System.out.println(BigDecimalUtils.add(0.05,0.01));
        System.out.println(BigDecimalUtils.sub(0.05,0.01));
        System.out.println(BigDecimalUtils.mul(4.102,100.0));
        System.out.println(BigDecimalUtils.div(1.0,0.43));
    }

}
