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
import java.util.Timer;
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
 * 慕课网 Rxjava与RxAndroid
 * https://www.imooc.com/video/15526
 * <p>
 * 本demo测试RxJava基本使用方法
 * <p>
 * RxJava 的基本实现主要有三点：
 * <p>
 * 创建Observer
 * Observer: 观察者，它决定事件触发的时候将有怎样的行为。
 * RxJava 中的 Observer 接口的实现方式：
 * <p>
 * Subscriber:订阅者
 * 除了 Observer 接口之外，RxJava 还内置了一个实现了 Observer 的抽象类：Subscriber。
 * Subscriber 对 Observer 接口进行了一些扩展，但他们的基本使用方式是完全一样的：
 * <p>
 * 创建Observable
 * Observable 即被观察者，它决定什么时候触发事件以及触发怎样的事件。
 * RxJava 使用 create() 方法来创建一个 Observable ，并为它定义事件触发规则：
 * <p>
 * 2018/11/22 重新学习RxJava、RxAndroid 以后会持续更新
 *
 *
 * https://blog.csdn.net/xmxkf/article/details/51656736#2-join
 * RxJava操作符(05-结合操作)
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.e(TAG, "main Thread =" + Thread.currentThread().getName());  // E/com.example.jh.rxjava.MainActivity: main Thread =main

//        test();

        // 写一个Hello World
        // 第一步创建被观察者
        /**
         * 操作符分类creating Observables (创建Observables)  创建型操作符
         * Create、Just、From、Defer、Empty/Never/Throw/
         * Interval、Range、Repeat、Start、Timer
         *
         * Start与Timer可以参考官方文档
         *
         */

//        first();
//        firstHello();
//        create();  // create()方法
//        just();    // just方法相当于快捷键
//        from();    // from 类型转换
//        defer();   // defer方法是字符串定义后在执行，just方法是先执行，后字符串定义
//        never();
//        interval();
//        timer();
//        range();

        /**
         * 转换操作符 Transforming Observables(转换Observable)
         */
//        map();    // 一对一    把一个对象装转化成我们想要的对象
//        flatMap();  // FlatMap 一对多  把一个对象装转化成Observable
//        groupBy();  // 分组
//        buffer();    //buffer  将数据进行2个值进行分组
//        scan();   // 累加
//        window();

        /**
         * 过滤Observable
         * 过滤性操作符：
         * Debounce(在操作间隔一定的时间内没有做任何操作就发送给观察者)
         * Distinct 去掉重复数据；
         * ElementAt 取指定位置的数据
         * Filter 按照指定规则过滤数据
         * First 列表数据中的第一个数据
         * IgnoreElements 忽略掉列表所有元素，不向观察者发送任何数据项，
         * 只回调onComplete和onError，不回调onNext。
         * Last 取最后一个数据
         * Sample 对数据进行定时取样
         * Skip 跳跃数据项的数据，
         * SkipLast 跳过指定数据项
         * Take 取值
         * TakeLast 取列表中的最后几个数据。
         */
//        debounce();
//        Distinct();
//        ElementAt();
//        Filter();
//        First();
//        IgnoreElements();
//        Last();
//        Sample();
//        Skip();
//        SkipLast();
//        Take();
//        TakeLast();

        /**
         * Combining Observables (组合Observable)
         *
         * zip   用来合并2个Observable发射的数据项，根据Func2函数生成一个新的值并发射出去，
         * 当其中一个Observable发送数据结束或者出现异常后，另一个Observable也将停止发射数据。
         * merge        2组数据源合并成一组新的数据(按照时间维度排列)
         * startWith    插入数据设置在前面位置
         * combineLatest  2组数据临近的数据组合成一组新的数据
         * join         在一定的时间间隔之内指定的结合产生新的数据
         * GroupJoin    与join类似
         * switchNext   小的Observable数据对象里组合成新的Observable对象
         */

        zip();
        merge();
        startWith();
        combineLatest();
        join();
        switchOnNext();

    }

    private void switchOnNext() {
        Observable.switchOnNext(Observable.create(
                new Observable.OnSubscribe<Observable<Long>>() {
                    @Override
                    public void call(Subscriber<? super Observable<Long>> subscriber) {
                        for (int i = 1; i < 3; i++) {
                            //每隔1s发射一个小Observable。小Observable每1s发射一个整数
                            //第一个小Observable将发射6个整数，第二个将发射3个整数
                            subscriber.onNext(Observable.interval(1000, TimeUnit.MILLISECONDS).take(i==1?6:3));
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
        )).subscribe(new Action1<Long>() {
            @Override
            public void call(Long s) {
                Log.v(TAG, "onNext:"+s);
                System.out.println("switchOnNext onCompleted " + s);
            }
        });
    }

    /**
     * join()函数基于时间窗口将2个Observable发射的数据组合在一起，
     * 每个Observable在自己的时间窗口内部都是有效的，都可以拿来组合
     */
    private void join() {
        //目标Observable
        Observable<Integer> obs1 = Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                for (int i = 1; i < 5; i++) {
                    subscriber.onNext(i);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
         //join
        Observable.just("srcObs-")
                .join(obs1,
                        //接受从源Observable发射来的数据，并返回一个Observable，
                        //这个Observable的生命周期决定了源Observable发射出来数据的有效期
                        new Func1<String, Observable<Long>>() {
                            @Override
                            public Observable<Long> call(String s) {
                                return Observable.timer(3000, TimeUnit.MILLISECONDS);
                            }
                        },
                        //接受从目标Observable发射来的数据，并返回一个Observable，
                        //这个Observable的生命周期决定了目标Observable发射出来数据的有效期
                        new Func1<Integer, Observable<Long>>() {
                            @Override
                            public Observable<Long> call(Integer integer) {
                                return Observable.timer(2000, TimeUnit.MILLISECONDS);
                            }
                        },
                        //接收从源Observable和目标Observable发射来的数据，并返回最终组合完的数据
                        new Func2<String,Integer,String>() {
                            @Override
                            public String call(String str1, Integer integer) {
                                return str1 + integer;
                            }
                        })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String o) {
                        Log.v(TAG,"join:"+o);
                        System.out.println("join onCompleted " + o);
                    }
                });

        //groupJoin
        Observable.just("srcObs-").groupJoin(obs1,
                new Func1<String, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(String s) {
                        return Observable.timer(3000, TimeUnit.MILLISECONDS);
                    }
                },
                new Func1<Integer, Observable<Long>>() {
                    @Override
                    public Observable<Long> call(Integer integer) {
                        return Observable.timer(2000, TimeUnit.MILLISECONDS);
                    }
                },
                new Func2<String,Observable<Integer>, Observable<String>>() {
                    @Override
                    public Observable<String> call(final String s, Observable<Integer> integerObservable) {
                        return integerObservable.map(new Func1<Integer, String>() {
                            @Override
                            public String call(Integer integer) {
                                return s + integer;
                            }
                        });
                    }
                })
                .subscribe(new Action1<Observable<String>>() {
                    @Override
                    public void call(Observable<String> stringObservable) {
                        stringObservable.subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                Log.v(TAG,"groupJoin:"+s);
                                System.out.println("groupJoin onCompleted " + s);
                            }
                        });
                    }
                });

    }

    /**
     * 用于将2个Observable最近发射的数据已经Func2函数的规则进行组合
     */
    // combineLatest onNext 7(5+2) 9(5+4) 11(5+6)
    private void combineLatest() {
        Observable<Integer> first = Observable.just(1,3,5);
        Observable<Integer> second = Observable.just(2,4,6);


        first.combineLatest(first, second, new Func2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer integer, Integer integer2) {
                System.out.println("integer " + integer + "integer2 " + integer2);
                return integer + integer2;
            }
        }).subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("combineLatest onCompleted ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("combineLatest onError " + e.getMessage());
                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("combineLatest onNext " + integer);
                    }
                });
    }

    /**
     * 用于在源Observable发射数据前插入数据。使用startWith(Interable<T>)我们
     * 还可以在源Observable发射的数据前插入Interable
     */
    // startWith onNext 2 4 6 1 3 5
    private void startWith() {
        Observable<Integer> first = Observable.just(1,3,5);
        Observable<Integer> second = Observable.just(2,4,6);

        first.startWith(second)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("startWith onCompleted ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("startWith onError " + e.getMessage());
                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("startWith onNext " + integer);
                    }
                });
    }

    /**
     * 将2个Observable发射的事件序列组合并成一个事件序列，就像是一个Observable发射的一样，
     * 你可以简单的将它理解为2个Observable合并成了一个Observable
     */
    // merge onNext 1 3 5 2 4 6
    public void merge(){
        Observable<Integer> odds = Observable.just(1,3,5);
        Observable<Integer> evens = Observable.just(2,4,6);

        Observable.merge(odds, evens)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("merge onCompleted ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("merge onError " + e.getMessage());
                    }

                    @Override
                    public void onNext(Integer integer) {
                        System.out.println("merge onNext " + integer);
                    }
                });
    }

    /*
     *  zip onNext 14 28，42
     */
    private void zip() {
        Observable<Integer> observable1 = Observable.just(10,20,30);
        Observable<Integer> observable2 = Observable.just(4,8,12,16);
        Observable.zip(observable1, observable2, new Func2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer integer, Integer integer2) {
                return integer + integer2;
            }
        }).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                System.out.println("zip onCompleted ");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("zip onError " + e.getMessage());
            }

            @Override
            public void onNext(Integer integer) {
                System.out.println("zip onNext " + integer);
            }
        });

    }

    /**
     * Range、repeat这里设置重复2次
     */
    private void range() {
        Observable.range(1, 10).repeat(2)
                .subscribe(new Subscriber<Integer>() {
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
    }

    private void timer() {
        Observable.timer(0, 5, TimeUnit.SECONDS)
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "timer ------->onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "timer onError: ");
                    }

                    @Override
                    public void onNext(Long aLong) {
                        Log.e(TAG, "timer ------>along：" + aLong + " time:" + SystemClock.elapsedRealtime());
                    }
                });
    }

    /**
     * 注意timer与interval都是默认运行在一个新线程上面
     * timer操作符既可以延迟执行一段逻辑，
     * 也可以间隔执行一段逻辑，但是已经过时了，而是由interval操作符来间隔执行.
     * timer延迟执行例子:如延迟5秒:
     */
    private void interval() {

        Observable.interval(0, 5, TimeUnit.SECONDS)
                .subscribe(new Observer<Long>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "interval onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "interval onError: ");
            }

            @Override
            public void onNext(Long aLong) {
                Log.e(TAG, "interval ------>along：" + aLong + " time:" + SystemClock.elapsedRealtime());
            }
        });
    }

    /**
     * Empty/Never/Throw/  代码执行过程中的捕获异常，不做显示，可查看源码
     */
    private void never() {
    }

    /**
     * Defer，比较下just与defer的区别
     * defer方法是字符串定义后在执行，just方法是先执行，后字符串定义
     * 可以把定义字符串放在方法后进行统一验证！
     */
    String str;

    private void defer() {

        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(1);
        arrayList.add(2);
        arrayList.add(3);
        arrayList.add(4);
        arrayList.add(5);
//        Observable observable1 = Observable.just(str);


        //@1与@2对比观察发现: defer是执行到subscribe方法时才会创建，所以str有值
        Observable observable = Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                Log.e(TAG, "defer call: " + str);
                return Observable.just(str);  // @1 E/com.example.jh.rxjava.MainActivity: defer onNext! ="测试值"
            }
        });

//        Observable observable = Observable.just(str);  // @2 E/com.example.jh.rxjava.MainActivity: defer onNext! =null
        str = "测试值";
        observable.subscribe(new Subscriber<String>() {
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
    }

    /**
     * From 类型转换，成为obseverable的对象
     * 数组、链表等，可以查看官方文档
     */
    private void from() {
        Observable.from(new Integer[]{1, 2, 3, 4, 5, 6}).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "from 整型 onCompleted!");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "from 整型 Error!");
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "from 整型 onNext! =" + integer);
            }
        });

        // 创建链表
        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(1);
        arrayList.add(2);
        arrayList.add(3);
        arrayList.add(4);
        arrayList.add(5);

        Observable.from(arrayList).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "from 链表 onCompleted!");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "from 链表 Error!");
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "from 链表 onNext! =" + integer);
            }
        });
    }

    /**
     * Just操作符,not create 直接运行，比较快捷。
     */
    private void just() {
        Observable.just("just方法 学习").subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, " just方法 onCompleted!");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "just方法 Error!");
            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, "just方法 onNext!");
            }
        });

    }

    private void create() {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("测试create方法");
            }
        }).subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, "onNext: " + s);
            }
        });
    }

    private void firstHello() {
        Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext("hello world");
                subscriber.onCompleted();         // 注释此行，会回调onError
//                throw new NullPointerException();  // 不会回调onError
                // 这就充分说明了在onNext方法执行后 onError 或者onCompleted只会执行一次就已经结束了。
            }
        });

        // 创建订阅者
        Subscriber subscriber = new Subscriber<String>() {

            @Override
            public void onCompleted() {
                Log.e(TAG, "subscriber onCompleted: ");   // E/com.example.jh.rxjava.MainActivity: onCompleted:
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "subscriber onError: ");
            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, "subscriber onNext: " + s);  // E/com.example.jh.rxjava.MainActivity: onNext: hello world
            }
        };

        // 创建观察者
        Observer observer = new Observer<String>() {

            @Override
            public void onCompleted() {
                Log.e(TAG, "observer onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "observer onError: ");
            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, "observer onNext: " + s);
            }
        };

        // 订阅事件
        observable.subscribe(subscriber);  // 订阅订阅者
        observable.subscribe(observer);   // 订阅观察者
    }



    private void test() {
//        window();   // 定时器。
        //————————————————————

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

    private void TakeLast() {
        Observable.just(10, 9, 9, 6, 6).takeLast(2)
                .subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "takeLast onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "takeLast onError");
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "takeLast onNext =" + integer + "\n");
            }
        });
    }

    private void Take() {
        Observable.just(10, 9, 9, 6, 6).take(2)
                .subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "take onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "take onError");
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "take onNext =" + integer + "\n");
            }
        });
    }

    private void SkipLast() {
        Observable.just(10, 9, 9, 6, 6).skipLast(2).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "skipLast onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "skipLast onError");
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "skipLast onNext =" + integer + "\n");
            }
        });
    }

    private void Skip() {
        Observable.just(10, 9, 9, 6, 6).skip(2)
                .subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "skip onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "skip onError");
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "skip onNext =" + integer + "\n");
            }
        });
    }

    private void Sample() {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> arg0) {
                try {
                    for (int i = 0; i < 10; i++) {
                        Thread.sleep(1000);
                        arg0.onNext(i);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    arg0.onError(e);
                }
            }
        }).sample(4, TimeUnit.SECONDS)
                .subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "sample onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "sample onCompleted");
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "sample onCompleted =" + integer);
            }
        });
    }

    private void Last() {
        Observable.just(10, 9, 9, 6, 6).last()
                .subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "last onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "last onError");
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "last onNext =" + integer + "\n");
            }
        });
    }

    private void IgnoreElements() {
        Observable.just(10, 9, 9, 6, 6).ignoreElements()
                .subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "ignoreElements onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "ignoreElements onError");
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "ignoreElements onNext =" + integer + "\n");
            }
        });
    }

    private void First() {
        Observable.just(10, 9, 9, 6, 6).distinct().first()
                .subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "first onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "first onError");
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "first onNext =" + integer + "\n");
            }
        });
    }

    private void Filter() {
        Observable.just(10, 9, 8, 7, 6).distinct()
                .filter(new Func1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer integer) {
                return integer > 8;
            }
        }).subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "filter onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "filter onError");
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "filter onNext =" + integer + "\n");
            }
        });
    }

    private void ElementAt() {
        Observable.just(10, 9, 8, 7, 6).elementAt(3)
                .subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "elementAt onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "elementAt onError");
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "elementAt onNext =" + integer + "\n");
            }
        });
    }

    private void Distinct() {
        Observable.just(1, 2, 3, 2, 3).distinct()
                .subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "distinct onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "distinct onError");
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "distinct onNext =" + integer + "\n");
            }
        });
    }

    private void debounce() {
        Observable.create(new Observable.OnSubscribe<Integer>() {

            @Override
            public void call(Subscriber<? super Integer> arg0) {
                try {
                    for (int i = 0; i < 10; i++) {
                        Thread.sleep(1000);
                        arg0.onNext(i);
                    }
                    arg0.onCompleted();
                } catch (Exception e) {
                    arg0.onError(e);
                }
            }
        }).debounce(1, TimeUnit.SECONDS)
                .subscribe(new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.e(TAG, "debounce onCompleted");
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "debounce onError =" + e + "\n");
            }

            @Override
            public void onNext(Integer integer) {
                Log.e(TAG, "debounce onNext =" + integer + "\n");
            }
        });
    }

    /**
     *  window
     *  window操作符会在时间间隔内缓存结果，
     *  类似于buffer缓存一个list集合，区别在于window将这个结果集合封装成了observable
     *  window(long timespan, TimeUnit unit)
     *  第一个是缓存的间隔时间，第二个参数是时间单位
     */
    private void window() {
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
    }


    private void scan() {
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
                // scan onNext =1
                // scan onNext =3
                // scan onNext =6
                // scan onNext =10
                // scan onNext =15
            }
        });
    }

    /**
     * buffer  将数据进行2个值进行分组
     */
    private void buffer() {
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
                // E/com.example.jh.rxjava.MainActivity: buffer onNext =[1, 2]
                // E/com.example.jh.rxjava.MainActivity: buffer onNext =[3, 4]
                // E/com.example.jh.rxjava.MainActivity: buffer onNext =[5]
            }
        });
    }

    /**
     *  GroupBy 对数据进行分组
     */
    private void groupBy() {
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
                        Log.e(TAG, "group:" + integer.getKey() + "---" + "data =" + data);
                    }
                });
            }
        });



    }

    /**
     * FlatMap 一对多 创建一个列表
     */
    private void flatMap() {
        Observable.just(1, 2, 3, 4, 5).flatMap
                (new Func1<Integer, Observable<? extends String>>() {
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
    }

    /**
     *  转换Observable
     *  类型: Map、FlatMap、GroupBy、Buffer、Scan、Window
     */

    // Map 一对一  整型转化成String
    private void map() {
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
    }



    private void first() {
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
    }


}
