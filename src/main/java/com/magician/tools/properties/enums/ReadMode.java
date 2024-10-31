package com.magician.tools.properties.enums;

/**
 * 加载properties文件的模式
 *
 * RESOURCE: 读取resource目录下的文件
 * LOCAL: 读取本机目录下的文件
 * REMOTE: 读取远程文件（http协议）
 */
public enum ReadMode {

    RESOURCE, LOCAL, REMOTE
}
