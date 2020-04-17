package com.fotile.x1i.listener;
/**
  * 文件名称：OnDialogListener
  * 创建时间：2019/5/27 19:22
  * 文件作者：yaohx
  * 功能描述：dialog两个按钮的回调
  */
public interface OnDialogListener {

    abstract void onLeftClick(Object... objects);

    abstract void onRightClick(Object... objects);

}
