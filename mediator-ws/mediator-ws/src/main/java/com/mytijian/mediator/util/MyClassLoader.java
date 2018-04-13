package com.mytijian.mediator.util;

public class MyClassLoader extends ClassLoader {

	private byte[] classByte;

	public byte[] getClassByte() {
		return classByte;
	}

	public void setClassByte(byte[] classByte) {
		this.classByte = classByte;
	}

	public Class<?> findClass(String name) throws ClassNotFoundException {
		byte[] b = getClassByte(); // 查找或生成Java类的字节代码
		return defineClass(name, b, 0, b.length);
	}
}
