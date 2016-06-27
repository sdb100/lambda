# lambda demo
This is a very simple demo example of lambdas in Java 8. I first started using J8 lambdas in a prototype [vert.x 3](http://vertx.io/) project, and this example is based on something I read at the time but can no longer find. I'm putting lambdas in the context of asynchronous callbacks - I came to J8 direct from doing a lot of work in JavaScript so callbacks were a natural way of doing things, and I think that they're a useful way of looking at the subject without getting heavily into streams or functional theory.

###Dispatcher###

The *Dispatcher* class is meant to represent a caller of an API - the kind of thing you might do in http request scope, for instance. It calls an instance of *Controller*, which implements synchronous and asynchronous API methods. The first call in *Controller* is the synchronous example. It just sends off the request and waits for the response. This is nice and simple but of course at some point it's going to block the requesting thread, which is disastrous if you're in something like an event loop. What you want to do is to specify a callback to be executed when the API call is complete. The idea is that you can cleanly hand off the API call to another thread, or non-blocking IO, or whatever, and get notified via the callback when it completes. In Java 7 (and before) you can do something like this, which uses the asynchronous API on *Controller*:

```java
    controller.doAsyncThing(t2, new Consumer<Result>() {
        public void accept(Result r) {
            that.output(r);
        }
    });
  ```
  The anonymous inner class implements the *Consumer* interface, as required by the API, and its *accept* method. Once a result is available, this will be called asynchronously by the executing thread. Note the use of **that**, which is declared *final* above. The inner class will close over the variable happily so long as it is declared final, so the callback thread can do things in the scope of the original caller method. Note also, that I can't use **this** for access to the enclosing class, because in context, **this** refers to the anonymous inner class.
  
  The next call uses a Java 8 lambda, and it looks like this:
  
  ```java
    controller.doAsyncThing(t3, (Result r) -> {
        this.output(r);
    });
```
  A good way of thinking about this is to go back to the anonymous inner class:
  
  ```java
    new Consumer<Result>() {
        public void accept(Result r) {
            that.output(r);
        }
    }
  ```

  Because the complier knows that the API wants a *Consumer<Result>* it can do without **new Consumer<Result>** part:

  ```java
    public void accept(Result r) {
        that.output(r);
    }
  ```
  But it also knows that the class only has one method, so there's no need to specify that either, because the compiler can work it out:
  
```java
    (Result r) {
        that.output(r);
    }
  ```
  The arrow tells the compiler you're using a lambda, and there it is:
  
```java
    (Result r) -> {
        this.output(r);
    }
  ```
  (Note the change in scope I've slipped in there: in a lambda, **this** refers to the enclosing class, not the lambda itself. Therefore I don't have to use **that**)
  
  More technically, what's happening here is that the compiler recognises a [functional interface](https://docs.oracle.com/javase/8/docs/api/java/util/function/package-summary.html) i.e. an interface with a single abstract method. It therefore knows that it can construct a lambda implementation for the interface.
  
  In fact, the next call simplifies things even further, because the compiler doesn't need you to specify *Result* - it can work this out as well.
  
  The last call uses a [method reference](https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html):
  
```java
    controller.doAsyncThingJ8Style(t5, System.out::println);
```
This is even more compact. We've shortcut the whole lambda construct, and just passed a reference to the *println* method. We can do this because *println* conforms to the *Consumer* interface, so the compiler can infer the parameter correctly and call it. In fact, *System.out::println* is semantically exactly the same as:

```java
    (s) -> {
        System.out.println(s);
    }
```
  
###Controller###

Look at the *Controller* interface and its implementation *ControllerImpl*. This serves as the API you need to call in this example. The synchronous *doSyncThing* method is a nice familiar synchronous API method: you call it with a *Request* and it returns a *Result*. Then there are two asynchronous API methods: *doAsyncThing* and *doAsyncThingJ8Style* which do basically the same thing but the implementations are different (ok so maybe I should have used a different implementation class or something but we're not being that picky here) 

I've implemented the asynchronous behavious with an *ExecutorService* and thread pool. The incoming requests are wrapped in a *Runnable* and then submitted to the ExecutorService for future processing by one of its threads. None of this is meant to be particularly exemplary - it just provides somthing that behaves in an asynchronous way. The callback API uses the Java 8 *Consumer* interface, which here is a convenient off-the-shelf way of doing it. *Consumer* just specifies a void *apply* method which is called with a single parameter, so there is no return value. The important thing is that it's a functional interface.

The two versions of the asynchronous API method are really there as a (further) demo of how you can simplify things using lambdas in Java 8. The Java 8 style version doesn't need to use the *Processor* inner class and so is cleaner and more compact.
