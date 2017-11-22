package com.example.jh.rxjava;

import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observables.GroupedObservable;

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
        Log.e(TAG, "main Thread =" + Thread.currentThread().getName());
        /**
         * 1、创建被观察者   Observable: 被观察者
         * 操作符分类
         * 创建Observable的操作符
         * Create、Just、From、Defer、Empty/Never/Throw/
         * Interval、Range、Repeat、Start、Timer
         *
         */
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


        // 2、创建观察者     Observer: 观察者
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
        // 3、添加订阅      Subscriber: 订阅者
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

        // 只有观察者与订阅者被观察者订阅的话，才会进行方法的调用。
        // 下面代码会调用call方法
        observable.subscribe(observer);     // 订阅观察者
        observable.subscribe(subscriber);   // 订阅订阅者


        /**
         * Just操作符,not create 直接运行，比较快捷。
         */
        Observable.just("RxJava学习").subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, " RxJava学习 onCompleted!");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "RxJava学习 Error!");
            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, "RxJava学习 onNext!");
            }
        });

        /**
         * From 类型转换，成为obseverable的对象
         * 数组、链表等，可以查看官方文档
         */
//        Observable.from(new Integer[]{1,2,3,4,5,6}).subscribe(new Subscriber<Integer>() {
//            @Override
//            public void onCompleted() {
//                Log.e(TAG, "整型 onCompleted!");
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                Log.e(TAG, "整型 Error!");
//            }
//
//            @Override
//            public void onNext(Integer integer) {
//                Log.e(TAG, "整型 onNext! =" + integer);
//            }
//        });
//        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        ArrayList<Integer> arrayList = new ArrayList<>();   // 建议换成这种创建链表方式
        arrayList.add(1);
        arrayList.add(2);
        arrayList.add(3);
        arrayList.add(4);
        arrayList.add(5);
        Observable.from(arrayList).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "链表 onCompleted!");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "链表 Error!");
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "链表 onNext! =" + integer);
            }
        });
        /**
         * Defer，比较下just与defer的区别
         * defer方法是字符串定义后在执行，just方法是先执行，后字符串定义
         * 可以把定义字符串放在方法后进行统一验证！
         */

        ArrayList<Integer> arrayList1 = new ArrayList<>();   // 建议换成这种创建链表方式
        arrayList1.add(1);
        arrayList1.add(2);
        arrayList1.add(3);
        arrayList1.add(4);
        arrayList1.add(5);
//        Observable observable1 = Observable.just(str);
        final String str = "哈哈";
        Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                return Observable.just(str);
            }
        }).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "defer onCompleted!");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "defer onError!");
            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, "defer onNext! =" + s);
            }
        });

        /**
         * Empty/Never/Throw/  代码执行过程中的捕获异常，不做显示，可查看源码
         *
         * 注意timer与interval都是默认运行在一个新线程上面
         * timer操作符既可以延迟执行一段逻辑，
         * 也可以间隔执行一段逻辑，但是已经过时了，而是由interval操作符来间隔执行.
         * timer延迟执行例子:如延迟5秒:
         */
//        Observable.timer(0, 5, TimeUnit.SECONDS).subscribe(new Observer<Long>() {
//            @Override
//            public void onCompleted() {
//                Log.e(TAG, "------->onCompleted");
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onNext(Long aLong) {
//                Log.e(TAG, "------>along：" + aLong + " time:" + SystemClock.elapsedRealtime());
//            }
//        });

//        Observable.interval(0,5,TimeUnit.SECONDS).subscribe(new Observer<Long>() {
//            @Override
//            public void onCompleted() {
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onNext(Long aLong) {
//                Log.e(TAG, "------>along："+aLong+" time:"+SystemClock.elapsedRealtime());
//            }
//        });

        /**
         * Range、repeat这里设置重复2次
         */
        Observable.range(1, 10).repeat(2).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "range onCompleted!");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "range onNext! =" + integer);
            }
        });

        /**
         * Start与Timer只能参考官方文档了
         */
        // ————————————————————————————————————————

        /**
         *  转换Observable
         *  类型: Map、FlatMap、GroupBy、Buffer、Scan、Window
         */

        // Map 一对一
        Observable.just(123).map(new Func1<Integer, String>() {
            @Override
            public String call(Integer integer) {
                return integer + "";
            }
        }).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "map onCompleted!");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, "map onNext! =" + s);
            }
        });
        // FlatMap 一对多
        Observable.just(1, 2, 3, 4, 5).flatMap(new Func1<Integer, Observable<? extends String>>() {


            @Override
            public Observable<? extends String> call(Integer integer) {
                return Observable.just(integer + "");
            }
        }).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "flatMap onCompleted!");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, "flatMap onNext! =" + s);
            }
        });
        // GroupBy 对数据进行分组
        Observable.just(1, 2, 3, 4, 5).groupBy(new Func1<Integer, Integer>() {
            @Override
            public Integer call(Integer integer) {
                return integer % 2;
            }
        }).subscribe(new Observer<GroupedObservable<Integer, Integer>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(final GroupedObservable<Integer, Integer> integer) {
                integer.subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer data) {
                        Log.e(TAG, "group:" + integer.getKey() + "data =" + data);
                    }
                });
            }
        });

        //buffer  将数据进行2个值进行分组
        Observable.range(1, 5).buffer(2).subscribe(new Observer<List<Integer>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<Integer> integers) {
                Log.e(TAG, "buffer onNext =" + integers);
            }
        });

        //  scan  结果是1/3/6/10/15
        Observable.range(1, 5).scan(new Func2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer integer, Integer integer2) {
                return integer + integer2;  // 求和操作
            }
        }).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "scan onNext =" + integer);
            }
        });

        /**
         *  window
         *  window操作符会在时间间隔内缓存结果，
         *  类似于buffer缓存一个list集合，区别在于window将这个结果集合封装成了observable
         *  window(long timespan, TimeUnit unit)
         *  第一个是缓存的间隔时间，第二个参数是时间单位
         */


        Observable.interval(1, TimeUnit.SECONDS).take(10).window(3, TimeUnit.SECONDS).subscribe(new Observer<Observable<Long>>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "window------>onCompleted()");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "window------>onError()" + e);
            }

            @Override
            public void onNext(Observable<Long> integerObservable) {
                Log.e(TAG, "window------->onNext()");
                integerObservable.subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long integer) {
                        Log.e(TAG, "window------>call():" + integer);
                    }
                });
            }
        });

        /**
         * 过滤Observable
         * 过滤性操作符：
         * Debounce(在操作间隔一定的时间内没有做任何操作就发送给观察者)、
         * Distinct 去掉重复
         * ElementAt 取指定位置的数据
         */

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
        String[] names = {"a", "b", "c"};
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
