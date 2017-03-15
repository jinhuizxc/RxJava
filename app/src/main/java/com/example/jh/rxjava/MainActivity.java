package com.example.jh.rxjava;

import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;

/**
 * 本demo测试RxJava基本使用方法
 * RxJava 的基本实现主要有三点：
 * <p>
 * 创建 Observer
 * Observer: 观察者，它决定事件触发的时候将有怎样的行为。
 * RxJava 中的 Observer 接口的实现方式：
 * <p>
 * Subscriber:订阅者
 * 除了 Observer 接口之外，RxJava 还内置了一个实现了 Observer 的抽象类：Subscriber。
 * Subscriber 对 Observer 接口进行了一些扩展，但他们的基本使用方式是完全一样的：
 * <p>
 * 创建 Observable
 * Observable 即被观察者，它决定什么时候触发事件以及触发怎样的事件。
 * RxJava 使用 create() 方法来创建一个 Observable ，并为它定义事件触发规则：
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e(TAG,"main Thread =" + Thread.currentThread().getName());
        // Observer: 观察者
        Observer<String> observer = new Observer<String>() {
            @Override
            public void onNext(String s) {
                Log.e(TAG, "观察者 Item: " + s);
            }

            @Override
            public void onCompleted() {
                Log.e(TAG, "观察者 Completed!");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "观察者 Error!");
            }
        };
        // Subscriber: 订阅者
        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onNext(String s) {
                Log.e(TAG, "订阅者 Item: " + s);
            }

            @Override
            public void onCompleted() {
                Log.e(TAG, "订阅者 Completed!");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "订阅者 Error!");
            }
        };


        // Observable: 被观察者
        Observable observable = Observable.create(new Observable.OnSubscribe<String>() {

            @Override
            public void call(Subscriber<? super String> subscriber) {
                Log.e(TAG, "call方法被执行");
                subscriber.onNext("Hello");
                subscriber.onNext("java");
                subscriber.onNext("android");
                subscriber.onCompleted();
                Log.e(TAG, "onCompleted");
            }
        });
        // 只有观察者与订阅者被观察者订阅的话，才会进行方法的调用。
        // 下面代码会调用call方法
        observable.subscribe(observer);
        observable.subscribe(subscriber);


        // ##############################################
        /**
         * Action0 是 RxJava 的一个接口，它只有一个方法 call()，
         * 这个方法是无参无返回值的；由于onCompleted()方法也是无参无返回值的，
         * 因此 Action0 可以被当成一个包装对象，
         * 将 onCompleted() 的内容打包起来将自己作为一个参数传入subscribe()以实现不完整定义的回调。
         * 这样其实也可以看做将onCompleted()方法作为参数传进了 subscribe()，
         * 相当于其他某些语言中的『闭包』。
         *
         */
        Action1<String> onNextAction = new Action1<String>() {
            // onNext()
            @Override
            public void call(String s) {
                Log.e(TAG, "Action1 =" + s);
            }
        };
        Action1<Throwable> onErrorAction = new Action1<Throwable>() {
            // onError()
            @Override
            public void call(Throwable throwable) {
                // Error handling
                Log.e(TAG, "Action1 onErrorAction completed");
            }
        };
        Action0 onCompletedAction = new Action0() {
            // onCompleted()
            @Override
            public void call() {
                Log.e(TAG, "Action0 onCompletedAction completed");
            }
        };

// 自动创建 Subscriber ，并使用 onNextAction 来定义 onNext()
        observable.subscribe(onNextAction);
// 自动创建 Subscriber ，并使用 onNextAction 和 onErrorAction 来定义 onNext() 和 onError()
        observable.subscribe(onNextAction, onErrorAction);
// 自动创建 Subscriber ，并使用 onNextAction、 onErrorAction 和 onCompletedAction 来定义 onNext()、 onError() 和 onCompleted()
        observable.subscribe(onNextAction, onErrorAction, onCompletedAction);

        //###################################################

//        a. 打印字符串数组
//        将字符串数组 names 中的所有字符串依次打印出来：
        String[] names ={"a", "b", "c"};
        Observable.from(names)
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String name) {
                        Log.e(TAG, name);
                    }
                });

        //b. 由id取得图片并显示
        // 由指定的一个drawable文件 id drawableRes取得图片，并显示在ImageView中，
        // 并在出现异常的时候打印Toast 报错：
//         final int drawableRes = R.drawable.e;
//         final ImageView imageView = (ImageView) findViewById(R.id.imageView);
//        Observable.create(new Observable.OnSubscribe<Drawable>() {
//            @Override
//            public void call(Subscriber<? super Drawable> subscriber) {
              // 这里需要  minSdkVersion为21
//                Drawable drawable = getTheme().getDrawable(drawableRes);
//                subscriber.onNext(drawable);
//                subscriber.onCompleted();
//            }
//        }).subscribe(new Observer<Drawable>() {
//            @Override
//            public void onNext(Drawable drawable) {
//                imageView.setImageDrawable(drawable);
//            }
//
//            @Override
//            public void onCompleted() {
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
//            }
//        });


        // -------------------------------------------------------------------

        /**
         *
         * 以上代码的测试包括数据发送接收都是在main主线程中进行！
         *
         * 在 RxJava 的默认规则中，事件的发出和消费都是在同一个线程的。
         * 也就是说，如果只用上面的方法，实现出来的只是一个同步的观察者模式。
         * 观察者模式本身的目的就是『后台处理，前台回调』的异步机制，
         * 因此异步对于 RxJava 是至关重要的。而要实现异步，
         * 则需要用到 RxJava 的另一个概念： Scheduler 。
         */
    }


}
