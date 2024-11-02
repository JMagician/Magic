<h1> 
    <a href="https://magician-io.com">Magic</a> ·
    <img src="https://img.shields.io/badge/licenes-MIT-brightgreen.svg"/>
    <img src="https://img.shields.io/badge/jdk-17+-brightgreen.svg"/>
    <!-- <img src="https://img.shields.io/badge/maven-3.5.4+-brightgreen.svg"/> -->
    <img src="https://img.shields.io/badge/release-master-brightgreen.svg"/>
</h1>

Magic是Magician旗下的一个工具包，支持并发处理、生产者与消费者模型、数据库操作等

## 运行环境

JDK17

## 文档

[https://magician-io.com/tools](https://magician-io.com/tools)

## 示例

### 01 导入依赖
```xml
<dependency>
    <groupId>com.github.yuyenews</groupId>
    <artifactId>Magic</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 02 数据库操作

#### 首先需要创建一个Spring的JdbcTemplate对象

```java
@Resource
private JdbcTemplate jdbcTemplate;
```

#### 单表查询

```java
// 创建查询条件
ConditionBuilder conditionBuilder = ConditionBuilder.createCondition()
                .add("age = ?", "300")
                .add(" order by age desc", Condition.NOT_WHERE);

// 执行查询操作
List<DemoPO> demoPOS = DBUtils.get(jdbcTemplate)
        .select("m_user_info", conditionBuilder, DemoPO.class);
```

#### 单表插入

```java
// 创建要插入的对象和值
DemoPO demoPO = DemoPO.builder()
                    .id(UUID.randomUUID().toString())
                    .age(18)
                    .content("哈哈哈")
                    .amount(new BigDecimal("10000"))
                    .length(new BigInteger("10000000000"))
                    .createTime(new Date())
                    .updateTime(new Date())
                    .build();

// 执行插入操作
DBUtils.get(jdbcTemplate).insert("m_user_info", demoPO);
```

#### 单表更新

```java
// 创建修改条件
ConditionBuilder = conditionBuilder = ConditionBuilder.createCondition()
                .add("id = ? and age = ?", "00df4362-d7ad-48d2-8bcb-05cf859b7e64", 500);

// 需要修改的字段和对应的值
DemoPO demoPO = DemoPO.builder()
        .age(122)
        .content("嘿嘿嘿")
        .amount(new BigDecimal("100002.33"))
        .length(new BigInteger("100000000002"))
        .createTime(new Date())
        .updateTime(new Date())
        .build();

// 执行修改操作
DBUtils.get(jdbcTemplate).update("m_user_info", demoPO, conditionBuilder);
```

#### 单表删除

```java
// 创建删除条件
ConditionBuilder = conditionBuilder = ConditionBuilder.createCondition()
                .add("id = ?", "00df4362-d7ad-48d2-8bcb-05cf859b7e64");
// 执行删除操作
DBUtils.get(jdbcTemplate).delete("m_user_info", conditionBuilder);
```

#### 自定义SQL查询

```java
DemoPO demoPO = DemoPO.builder()
                .age(122)
                .build();

DBUtils.get(jdbcTemplate).selectList("select * from m_user_info where age > {age}", demoPO, DemoPO.class);
```

#### 自定义SQL分页

```java
// 查询参数
PageParamModel pageParamModel = PageParamModel
                .getPageParamModel(1, 10)
                        .setParam(
                                DemoPO.builder()
                                    .age(10)
                                    .build()
                        );

// 执行查询操作
PageModel<DemoPO> pageModel = DBUtils.get(jdbcTemplate).selectPage("select * from m_user_info where age > {age}", pageParamModel, DemoPO.class);
```

### 03 并发处理任务

```java
MagicianDataProcessing.getConcurrentTaskSync()
                .setTimeout(1000) // 超时时间
                .setTimeUnit(TimeUnit.MILLISECONDS) // 超时时间的单位
                .add(() -> { // 添加一个任务

                    // 在这里可以写上任务的业务逻辑

                }, (result, e) -> {
                    // 此任务处理后的回调
                    if(result.equals(ConcurrentTaskResultEnum.FAIL)){
                        // 任务失败，此时e里面有详细的异常信息
                    } else if(result.equals(ConcurrentTaskResultEnum.SUCCESS)) {
                        // 任务成功，此时e是空的
                    }
                })
                .add(() -> { // 添加一个任务

                    // 在这里可以写上任务的业务逻辑

                }, (result, e) -> {
                    // 此任务处理后的回调
                    if(result.equals(ConcurrentTaskResultEnum.FAIL)){
                        // 任务失败，此时e里面有详细的异常信息
                    } else if(result.equals(ConcurrentTaskResultEnum.SUCCESS)) {
                        // 任务成功，此时e是空的
                    }
                })
                .start();
```

### 04 并发处理List，Set等所有Collection类集合里的元素

```java
// 假如有一个List需要并发处理里面的元素
List<String> dataList = new ArrayList<>();

// 只需要将他传入syncRunner方法即可，每个参数的具体含义可以参考文档
MagicianDataProcessing.getConcurrentCollectionSync()
        .syncRunner(dataList, data -> {

            // 这里可以拿到List里的元素，进行处理
            System.out.println(data);
        
        }, 10, 1, TimeUnit.MINUTES);

// 也可以用syncGroupRunner方法，每个参数的具体含义可以参考文档
MagicianDataProcessing.getConcurrentCollectionSync()
        .syncGroupRunner(dataList, data -> {

            // 这里是每一组List
            for(String item : data){
                // 这里可以拿到List里的元素，进行处理
                System.out.println(data);
            }
        
        }, 10, 1, TimeUnit.MINUTES);
```

### 05 生产者与消费者

```java
// 创建一组生产者与消费者，支持多对多
MagicianDataProcessing.getProducerAndConsumerManager()
        .addProducer(new MagicianProducer() { // 添加一个生产者（可以添加多个）
            
            @Override
            public void producer() {
                // 查询这张表里符合条件的数据
                List<Object> dataList = selectList();
        
                // 然后将他推送给消费者
                this.publish(dataList);
            }
            
        }).addConsumer(new MagicianConsumer() { // 添加一个消费者（可以添加多个）
            
            @Override
            public void doRunner(Object data) {
                // 处理生产者发来的数据
                System.out.println(data);
            }
            
        }).start();
```