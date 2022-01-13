#### whale-xss使用文档

##### 一、简单介绍
- 对表单绑定的字符串类型进行 xss 处理。
- 对json字符串数据进行 xss 处理。
- 提供路由和控制器方法级别的放行规则。

##### 二、使用
**maven**
```
<dependency>
            <groupId>com.sensetime.iva.whale</groupId>
            <artifactId>whale-xss</artifactId>
            <version>1.0-SNAPSHOT</version>
</dependency>
```
**配置**
配置项 | 默认值 | 说明   
whale.xss.enabled | true | 开启xss
whale.xss.path-patterns | /**   | 拦截的路由，例如: /api/**
whale.xss.path-exclude-patterns |   | 放行的路由，默认为空

**注解**

可以使用 @XssCleanIgnore 注解对方法和类级别进行忽略。

**自定义 xss 清理**

如果内置的 xss 清理规则不满足需求，可以自己实现 XssCleaner，注册成 Spring bean 即可。

1. 注册成 Spring bean
@Bean
public XssCleaner xssCleaner() {
    return new MyXssCleaner();
}

2. MyXssCleaner
public class MyXssCleaner implements XssCleaner {

	@Override
	public String clean(String html) {
		//xss清除处理逻辑
		return html;
	}

}