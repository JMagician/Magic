# Magic

## 运行环境

JDK17+

## 初始化配置

### 导入依赖

```xml
<dependency>
    <groupId>com.github.yuyenews</groupId>
    <artifactId>Magic</artifactId>
    <version>1.0.3</version>
</dependency>
```

## 并发处理任务

```java
MagicDataProcessing.getConcurrentTaskSync()
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

添加进去的任务会并发执行，但是在它们执行完之前，这整个代码块会同步等待在这，一直等到所有任务执行完或者超时才会继续往下走。

这里面的超时时间就是用来设置同步等待多久的。

- 如果设置为0表示一直等到所有任务完成为止
- 设置为大于0的时候，表示只等待这么久



## 并发处理List，Set等所有Collection类的集合里的元素

### 同步执行

假如有一个List需要并发处理里面的元素

```java
List<String> dataList = new ArrayList<>();
```

只需要将他传入syncRunner方法即可

```java
MagicDataProcessing.getConcurrentCollectionSync()
        .syncRunner(dataList, data -> {

            // 这里可以拿到List里的元素，进行处理
            // List里的元素是什么类型，这个data就是什么类型
            System.out.println(data);
        
        }, 
        10, // 每组多少条元素
        1, // 每组之间同步等待多久
        TimeUnit.MINUTES // 等待的时间单位
        );
```

这个方法会将传进去的集合分成若干组，每组的大小由参数指定。

这些组会排队执行，但是每一组在执行的时候都是并发的，里面的每一个元素都会由单独的线程去处理。

需要等一组处理完了，才会处理下一组，但是有时候我们不想这么死板的等待，所以可以设置一个超时时间，超过了这个期限就不等了，直接进行下一组，所以这里的最后两个参数就是用来设置这个期限的。

#### 也可以让每一组单独占一个线程

```java
// 也可以用syncGroupRunner方法
MagicDataProcessing.getConcurrentCollectionSync()
        .syncGroupRunner(dataList, data -> {

            // 这里是每一组List
            for(String item : data){
                // 这里可以拿到List里的元素，进行处理
                System.out.println(data);
            }
        
        }, 
        10, // 每组多少条元素
        1, // 每组之间同步等待多久
        TimeUnit.MINUTES // 等待的时间单位
        );
```

这个方法会将传进去的集合分成若干组，每组的大小由参数指定。

每一组由单独的线程处理。

会一直同步等待在这里，直到所有组都处理完了才会进行下一步，但是有时候我们不想这么死板的等待，所以可以设置一个超时时间，超过了这个期限就不等了，直接执行下一步。所以这里的最后两个参数就是用来设置这个期限的。

### 异步执行

其实就是将上面【同步执行】的代码放到了一个线程里，内部处理依然是上面【同步执行】的逻辑，但是这整个代码块将会异步执行，不需要等在这。所以个别相同的参数就不再重复解释了。

```java
// 假如有一个List需要并发处理里面的元素
List<String> dataList = new ArrayList<>();
```

#### 每个元素并发执行

```java
// 只需要将他传入asyncRunner方法即可
MagicDataProcessing.ConcurrentCollectionAsync().asyncRunner(dataList, data -> {

            // 这里可以拿到List里的元素，进行处理
            System.out.println(data);
    
        }, 
        10, // 每组多少条元素
        1, // 每组之间同步等待多久
        TimeUnit.MINUTES // 等待的时间单位
        )
        .start();// 注意，异步执行需要调用start方法
```

还可以这样写

```java
MagicDataProcessing.ConcurrentCollectionAsync().asyncRunner(dataList, data -> {

            // 这里是每一组List
            for(String item : data){
                // 这里可以拿到List里的元素，进行处理
                System.out.println(data);
            }
    
        }, 
        10, // 每组多少条元素
        1, // 每组之间同步等待多久
        TimeUnit.MINUTES // 等待的时间单位
        ).asyncRunner(dataList2, data -> {

            // 这里可以拿到List里的元素，进行处理
            System.out.println(data);
    
        }, 
        10, // 每组多少条元素
        1, // 每组之间同步等待多久
        TimeUnit.MINUTES // 等待的时间单位
        ).asyncRunner(dataList3, data -> {

            // 这里可以拿到List里的元素，进行处理
            System.out.println(data);
    
        }, 
        10, // 每组多少条元素
        1, // 每组之间同步等待多久
        TimeUnit.MINUTES // 等待的时间单位
        )
        .start(); // 一样要调用start方法
```

#### 每一组并发执行

```java
// 也可以用asyncGroupRunner方法，每个参数的具体含义可以参考文档
MagicDataProcessing.ConcurrentCollectionAsync().asyncGroupRunner(dataList, data -> {
        
            // 这里是每一组List
            for(String item : data){
                // 这里可以拿到List里的元素，进行处理
                System.out.println(data);
            }
        
        }, 
        10, // 每组多少条元素
        1, // 每组之间同步等待多久
        TimeUnit.MINUTES // 等待的时间单位
        )
        .start(); // 一样要调用start方法
```

同上

## 并发处理所有Map类的集合里的元素

Map的逻辑跟Collection一模一样，只不过是传入的集合变成了Map，就不再累述了，感谢理解。

### 同步执行

#### 每个元素并发执行

```java
// 假如有一个Map需要并发处理里面的元素
Map<String, Object> dataMap = new HashMap<>();

// 只需要将他传入syncRunner方法即可
MagicDataProcessing.getConcurrentMapSync()
        .syncRunner(dataMap, (key, value) -> {

            // 这里可以拿到Map里的元素，进行处理
            System.out.println(key);
            System.out.println(value);
        
        }, 10, 1, TimeUnit.MINUTES);
```

#### 每一组并发执行

```java
// 也可以用syncGroupRunner方法
MagicDataProcessing.getConcurrentMapSync()
        .syncGroupRunner(dataMap, data -> {

            // 这里是每一组Map
            for(Map.Entry<String, Object> entry : data.entrySet()){
                // 这里可以拿到Map里的每一个元素
                System.out.println(entry.getKey());
                System.out.println(entry.getValue());
            }
        
        }, 10, 1, TimeUnit.MINUTES);
```

### 异步执行

#### 每个元素并发执行

```java
// 假如有一个Map需要并发处理里面的元素
Map<String, Object> dataMap = new HashMap<>();

// 只需要将他传入asyncRunner方法即可
MagicDataProcessing.getConcurrentMapAsync().asyncRunner(dataMap, (key, value) -> {

            // 这里可以拿到Map里的元素，进行处理
            System.out.println(key);
            System.out.println(value);
    
        }, 
        10, 
        1, 
        TimeUnit.MINUTES
        )
        .start(); // 一样要调用start方法
```

#### 每一组并发执行

```java
// 也可以用asyncGroupRunner方法
MagicDataProcessing.getConcurrentMapAsync().asyncGroupRunner(dataMap, data -> {
        
            // 这里是每一组Map
            for(Map.Entry<String, Object> entry : data.entrySet()){
                // 这里可以拿到Map里的每一个元素
                System.out.println(entry.getKey());
                System.out.println(entry.getValue());
            }
        
        }, 
        10, 
        1, 
        TimeUnit.MINUTES
        )
        .start(); // 一样要调用start方法;
```

## 生产者与消费者

这是一个多对多的模型，多个生产者可以给多个消费者推送不同类型的数据，

### 我们先创建一个生产者
```java
public class DemoProducer extends MagicProducer {

    /**
     * 设置ID，必须全局唯一，默认是当前类的全名
     * 如果采用默认值，可以不重写这个方法
     * @return
     */
    @Override
    public String getId() {
        return super.getId();
    }

    /**
     * 设置producer方法是否重复执行，默认重复
     * 如果采用默认值，可以不重写这个方法
     * @return
     */
    @Override
    public boolean getLoop() {
        return super.getLoop();
    }

    /**
     * 设置 是否等消费者全部空闲了才继续生产下一轮数据，默认false
     * 如果采用默认值，可以不重写这个方法
     * @return
     */
    @Override
    public boolean getAllFree() {
        return super.getAllFree();
    }

    /**
     * 当生产者启动后，会自动执行这个方法，我们可以在这个方法里生产数据，并通过publish方法发布给消费者
     *
     * 这边举一个例子
     * 假如我们需要不断地扫描某张表，根据里面的数据状态去执行一些业务逻辑
     * 那么我们可以在这个方法里写一个查询的逻辑，然后将查询到数据发送给消费者
     */
    @Override
    public void producer() {
        // 根据上面的例子，我们可以查询这张表里符合条件的数据
        List<Object> dataList = selectList();

        // 然后将他推送给消费者
        // 可以推送任意类型的数据
        this.publish(dataList);

        /*
          * 如果你只需要执行一次，那么到此就结束了，这个生产者也可以被回收掉了
          *
          * 但是如果你需要不断地执行上述操作，来维护这张表里的数据，这个时候你有两种做法
          * 第一种：加一个while循环
          *      但是这种方式有个问题，如果消费者的消费速度跟不上，那么就很容易造成消费者队列积压，出现内存问题。
          *      而数据积压太久又会影响时效性，可能你推送给消费者的时候，这条数据需要处理，但是等到被消费的时候又不需要处理了，这样容易出现数据错乱的问题。
          *
          * 第二种：等消费者把你推给他的数据消费完了，再推送下一轮，而我们就是采用的这种
          *      如果你想用这种方式，那么你不需要再写其他的任何逻辑，只需要将上面提到的getLoop方法重写一下，并返回true即可
          *      当你设置为true以后，生产者在推送完一轮后会不断地监视消费者，当发现了空闲的消费者才会生产和推送下一轮数据，并且数据只会推送给这几个空闲的消费者
          *
          * 如果你想等所有消费者都空闲了以后再推送下一轮，而不是发现一个空闲的就推送一轮
          * 那么你可以重写上面提到的getAllFree方法，返回true即可
          */

    }
}
```

### 再创建一个消费者
```java
public class DemoConsumer extends MagicConsumer {
    /**
     * 设置ID，必须全局唯一，默认是当前类的全名
     * 如果采用默认值，可以不重写这个方法
     * @return
     */
    @Override
    public String getId() {
        return super.getId();
    }

    /**
     * 心跳通知，消费者每消费一个任务，都会触发一下这个方法
     * 我们可以根据他触发的频率来判断这个消费者的活跃度
     *
     * 注意！！！
     * 这个方法里不可以有耗时的操作，不然会将消费者阻塞的
     * 如果一定要加耗时的操作，那么务必在新线程里搞
     * @param id
     */
    @Override
    public void pulse(String id) {
        new Thread(()->{
            // 如果你需要在这个方法里搞一些耗时的操作，那么务必要像这样开启一个新线程
            // 不然消费者会被阻塞的
        }).start();
    }

    /**
     * 消费频率限制，默认10毫秒，取值范围：0 - long的最大值，单位：毫秒
     *
     * 如果任务执行的耗时小于execFrequencyLimit，则等待execFrequencyLimit毫秒后再消费下一个任务
     *
     * 首先这是一个生产者和消费者多对多的模型结构，我们以一个生产者对多个消费者来举例
     * 生产者生产的数据只有一份，但是他会推送给多个消费者
     * 而我们之所以要配置多个消费者，是因为需要他们执行不同的业务逻辑
     * 多个消费者执行的业务逻辑不同，也就意味着他们需要的数据大概率会不同
     *
     * 比如消费者A需要处理男性的数据，消费者B需要处理女性的数据
     * 如果生产者刚好连续推送了几批男性的数据，那么这会导致消费者B筛选不到女性数据，那么他就不会处理业务逻辑了
     * 这么一来，消费者B就会无限接近空转，而空转会引起CPU占用率过大，所以必须加以限制
     *
     * 千万不要小看这个问题，本人曾经在实战中亲测过，做不做这个限制，CPU的占有率会达到10倍的差距
     * 当然了，这跟消费者的业务逻辑还是有一定关系的，具体情况具体看待
     * 如果你的消费者几乎不会出现空转，那么这里可以设置为0
     *
     */
    @Override
    public long getExecFrequencyLimit() {
        return super.getExecFrequencyLimit();
    }

    /**
     * 这个方法会接收到生产者推送过来的数据
     * 在里面执行相应的业务逻辑即可
     * @param data
     */
    @Override
    public void doRunner(Object data) {
        // data 可以是任何类型
        // 因为能给他推送数据的消费者是固定的，所以data有可能收到的类型也是固定的
        // 所以我们可以在这里自己判断，然后转化即可
        // 为什么不用泛型？这是为了兼容多个生产者，因为他们推送的数据类型可能会不同
    }
}
```

### 然后将他们添加到同一个组内
```java
// 创建一组生产者与消费者，而这样组可以创建无限个
// 每一组的生产者都只会把数据推送给同一组的消费者
MagicDataProcessing.getProducerAndConsumerManager()
                .addProducer(new DemoProducer()) // 添加一个生产者（可以添加多个）
                .addConsumer(new DemoConsumer()) // 添加一个消费者（可以添加多个）
                .start();
```

## 数据库操作

此组件重度依赖于SpringBoot，底层是基于JdbcTemplate的扩展，做到了单表操作不需要写SQL，天然支持MySql分页查询，支持在SQL中写入{属性名}占位符，如果你不想用SpringBoot但是又想使用这个组件，可以用[Magician-JDBC](/db/index.md)

### 添加依赖

```xml
<!-- mysql driver package -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.20</version>
</dependency>

<!-- 支持任意连接池，这里只是举个例子，你平时该怎么用springboot就怎么用 -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>1.2.5</version>
</dependency>
```

### 创建一个Spring的JdbcTemplate对象

```java
@Resource
private JdbcTemplate jdbcTemplate;
```

### 单表无SQL操作

#### 插入数据

```java
ParamPO paramPO = new ParamPO();
paramPO.setUserName("a");
paramPO.setUserEmail("test@qq.com");

int result = MagicDBUtils.get(jdbcTemplate).insert("表名", paramPO);
```

### 修改数据

```java
// 构建修改条件
ConditionBuilder conditionBuilder = ConditionBuilder.createCondition()
        .add("id = ?", 10)
        .add("and name = ?", "bee");

// 构建修改数据
ParamPO paramPO = new ParamPO();
paramPO.setUserName("a");
paramPO.setUserEmail("test@qq.com");

// 执行修改
int result = MagicDBUtils.get(jdbcTemplate).update("表名", paramPO, conditionBuilder);
```

### 删除数据

```java
// 构建删除条件
ConditionBuilder conditionBuilder = ConditionBuilder.createCondition()
        .add("id = ?", 10);

// 执行删除
int result = MagicDBUtils.get(jdbcTemplate).delete("表名", conditionBuilder);
```

### 查询数据

```java
// 构建查询条件
ConditionBuilder conditionBuilder = ConditionBuilder.createCondition()
            .add("id > ?", 10)
            .add("and (name = ? or age > ?)", "bee", 10)
            .add("order by create_time", Condition.NOT_WHERE);

// 执行查询
List<ParamPO> result = MagicDBUtils.get(jdbcTemplate).select("表名", conditionBuilder, ParamPO.class);
```

### 条件构造器说明

内部结构如下

```java
public class Condition {
    // 条件，可以是 where， order by， group by 等任意条件
    private String key;
    // 如果条件设置的是where条件，那么这个属性就需要设置成 条件的值
    private Object[] val;

    // 如果条件不是where，那么val就必须设置成这个常量
    public static final String NOT_WHERE = "notWhere";
}
```

可以看如下示例

```java
ConditionBuilder conditionBuilder = ConditionBuilder.createCondition()
            // 这里key 设置成了where条件，所以val 就设置成了 where的值，也就是查询 id > 10 的数据
            .add("id > ?", 10)
            // 这里也一样的，是where条件，但是因为他是第二个条件，所以需要 在最前面加上and，or 等连接符
            .add("and (name = ? or age > ?)", "bee", 10)
            // 这是排序，所以 val需要设置成 Condition.NOT_WHERE
            .add("order by create_time", Condition.NOT_WHERE);
```

注：条件构造器只支持 ? 占位符

## 自定义sql

### 增删改

```java
ParamPO paramPO = new ParamPO();
paramPO.setUserName("testTx222");
paramPO.setUserEmail("testTx222@qq.com");
paramPO.setId(4);

// 采用{}占位符的写法
int result = MagicDBUtils.get(jdbcTemplate).exec("update xt_message_board set user_name = {user_name} , user_email = {user_email} where id = {id}", paramPO);

// 采用 ? 占位符的写法
int result = MagicDBUtils.get(jdbcTemplate).exec("update xt_message_board set user_name = ? , user_email = ? where id = ?", new Object[]{"testTx222","testTx222@qq.com", 4});
```

### 查询数据

```java
ParamPO paramPO = new ParamPO();
paramPO.setId(5);
paramPO.setUserName("a");

// 采用{}占位符的写法
List<ParamPO> result = MagicDBUtils.get(jdbcTemplate).selectList("select * from xt_message_board where id > {id} and user_name != {user_name}", paramPO, ParamPO.class);

// 采用 ? 占位符的写法
List<ParamPO> result = MagicDBUtils.get(jdbcTemplate).selectList("select * from xt_message_board where id > ? and user_name != ?", new Object[]{5, "a"}, ParamPO.class);
```

### 分页查询

```java
// 查询条件
ParamPO paramPO = new ParamPO();
paramPO.setId(5);
paramPO.setUserName("a");

// 查询参数
PageParamModel pageParamModel = new PageParamModel();
pageParamModel.setCurrentPage(1);
pageParamModel.setPageSize(10);
pageParamModel.setParam(paramPO);

// 使用默认countSql查询
PageModel<ParamPO> pageModel =  MagicDBUtils.get(jdbcTemplate).selectPage("select * from xt_message_board where id > {id} and user_name != {user_name}", pageParamModel, ParamPO.class);

// 使用自定义countSql查询
String countSql = "自己定义countSql";

PageModel<ParamPO> pageModel =  MagicDBUtils.get(jdbcTemplate).selectPageCustomCountSql("select * from xt_message_board where id > {id} and user_name != {user_name}", countSql, pageParamModel, ParamPO.class);

```

### SQL构造器

其实就是把StringBuilder封装了一层，目的是为了减少大量的if，让判断+拼接的过程缩短在一行上，以及在拼接时不再需要注意空格了

以实体对象作为参数的示例
```java
ParamPO paramPO = new ParamPO();
paramPO.setAge(10);
paramPO.setHeight(1);

SqlBuilder sqlBuilder = SqlBuilder.builder()
        .init("select * from user_info where 1=1") // 这里传入sql主体
        .append("and age > {age}", paramPO.getAge() > 0) // 第一个参数传入要拼接的where条件，第二个参数传入是否要拼接的判断条件，也就是说第二个参数的等式成立才会将第一个参数拼接到sql上去，下面都是一样的
        .append("and name = {name}", paramPO.getName() != null)
        .append("and height > {height}", paramPO.getHeight() != null);

// 直接toString 可以获取到拼接后的完整sql
String sql = sqlBuilder.toString();

// 查询数据
List<ParamPO> result = MagicDBUtils.get(jdbcTemplate).selectList(sql, paramPO, ParamPO.class);
```

以数组作为参数的示例
```java
List<Object> params = new ArrayList<>();

SqlBuilder sqlBuilder = SqlBuilder.builder()
        .init("select * from user_info where 1=1") // 这里传入sql主体
        
        // 这里为了偷懒，还是用到了paramPO对象，反正大家懂那个意思就行了
        // 第一个参数传入要拼接的where条件，第二个参数传入是否要拼接的判断条件
        // 也就是说第二个参数的等式成立才会将第一个参数拼接到sql上去，下面都是一样的
        // 第三个参数是一个回调函数，用来往params里添加数据的
        .append("and age > ?", paramPO.getAge() > 0, ()->{params.add(paramPO.getAge());}) 
        .append("and name = ?", paramPO.getName() != null, ()->{params.add(paramPO.getName());})
        .append("and height > ?", paramPO.getHeight() != null, ()->{params.add(paramPO.getHeight());});

// 直接toString 可以获取到拼接后的完整sql
String sql = sqlBuilder.toString();

// 查询数据，注意看第二个参数
List<ParamPO> result = MagicDBUtils.get(jdbcTemplate).selectList(sql, params.toArray(), ParamPO.class);
```

## 实体映射

完全用的是fastjson2的那一套的注解

```java
public class TestPO{

    @JSONField(name = "数据库里的name字段名")
    private String name;
    @JSONField(name = "数据库里的age字段名")
    private String age;
    @JSONField(name = "数据库里的id字段名")
    private int id;

    @JSONField(name = "create_time", format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
```

## 属性文件读取

### 加载配置文件

目前只支持 properties文件，你可以在任意目录下创建，然后使用以下方式将文件加载到项目中

从本机任意目录加载
```java
// 必须写文件的绝对路径
MagicProperties.load("/home/xxx/application.properties", ReadMode.LOCAL, "UTF-8");
```

从当前项目的资源目录下加载

```java
// 类资源下的文件的路径
MagicProperties.load("/application.properties", ReadMode.RESOURCE, "UTF-8");
```

从远程目录加载

```java
// 远程文件路径, 只支持http协议
MagicProperties.load("https://www.test.com/application.properties", ReadMode.REMOTE, "UTF-8");
```

### 根据key获取value

```java
// 如果配置文件里有userName这个key，那么就会直接使用，如果没有 那么会去环境变量读取
String userName = MagicProperties.get("userName");
```

### 遍历所有的key -> value

此法只能遍历文件有的配置项，获取不到环境变量

```java
MagicProperties.forEach((key, value)->{
            System.out.println(key);
            System.out.println(value);
        });
```