package com.hhd2002.androidbaselib.funcdelegate;

public interface IHhdFuncDelegateIn2Out<IN, IN2, OUT> {
    OUT execute(IN in, IN2 in2);
}
