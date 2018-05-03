# x5springboot
wex5后端，使用springboot替换baas服务

## 重构事项如下：
* 使用springboot作为baas后端基础框架（ORM框架为mybatis）
* 将$WEX5_HOME/source/baas.java.zip源码迁移至项目中，并进行相关删减
  * BaasServlet废弃，直接使用mvc中@Controller注解完成原baas服务的单表CURD各项集成服务
  * 原baas中自定义baas，使用mybatis自定义的interceptor，定制CURD的各项操作语义
  * 数据源使用druid，去除ActionContext（debug代码后发现只提供connection，未发现其他作用）。数据源一律使用@Autowired自动注入

# 涉及到UIServer中的配置。配置截图单独写博客作为配置教程
