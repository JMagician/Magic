<h1> 
    <a href="https://magician-io.com">Magician-Tools</a> ·
    <img src="https://img.shields.io/badge/licenes-MIT-brightgreen.svg"/>
    <img src="https://img.shields.io/badge/jdk-17+-brightgreen.svg"/>
    <!-- <img src="https://img.shields.io/badge/maven-3.5.4+-brightgreen.svg"/> -->
    <img src="https://img.shields.io/badge/release-master-brightgreen.svg"/>
</h1>

Magician-Tools 是一个用Java开发的工具包，支持并发处理、生产者与消费者模型、数据库操作等

## 运行环境

JDK17

## 文档

[https://magician-io.com/tools](https://magician-io.com/tools)

## 示例

### 导入依赖
```xml
<dependency>
    <groupId>com.github.yuyenews</groupId>
    <artifactId>Magician-Tools</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 并发处理任务

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

### 并发处理List，Set等所有Collection类集合里的元素

同步执行

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

异步执行

```java
// 假如有一个List需要并发处理里面的元素
List<String> dataList = new ArrayList<>();

// 只需要将他传入asyncRunner方法即可，每个参数的具体含义可以参考文档
MagicianDataProcessing.getConcurrentCollectionAsync()
        .asyncRunner(dataList, data -> {

            // 这里可以拿到List里的元素，进行处理
            System.out.println(data);
    
        }, 10, 1, TimeUnit.MINUTES)
        .start();

// 也可以用asyncGroupRunner方法，每个参数的具体含义可以参考文档
MagicianDataProcessing.getConcurrentCollectionAsync()
        .asyncGroupRunner(dataList, data -> {

            // 这里是每一组List
            for(String item : data){
                // 这里可以拿到List里的元素，进行处理
                System.out.println(data);
            }
        
        }, 10, 1, TimeUnit.MINUTES)
        .start();
```

### 生产者与消费者

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