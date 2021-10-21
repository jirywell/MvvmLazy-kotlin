
 
# MvvmLazy Android懒人框架(kotlin版)
目前，android流行的MVC、MVP模式的开发框架很多，然而一款基于MVVM模式开发框架却很少。 
个人搜寻了市面上大量的开源框架,秉承减少重复造轮子的原则,汲取了各位大神的框架优点,集成了大量常用的开源框架和工具类,进行了部分公用模块封装,丰富了BindingAdapter自定义数据绑定,创建了这套Android懒人开发框架,已在多个商业项目中经过检验,可靠性值得信赖.  
**MvvmLazy是以谷歌DataBinding+LiveData+ViewModel框架为基础，整合Okhttp+协程+Retrofit+Glide等流行模块，加上各种原生控件自定义的BindingAdapter，让事件与数据源完美绑定的一款容易上瘾的实用性MVVM快速开发框架**。从此告别findViewById()，告别setText()，告别setOnClickListener()...  

[**java版本**](https://github.com/jirywell/MvvmLazy)

## 框架特点
- **快速开发**

	只需要写项目的业务逻辑，不用再去关心网络请求、权限申请、View的生命周期等问题，撸起袖子就是干。

- **维护方便**

	MVVM开发模式，低耦合，逻辑分明。Model层负责将请求的数据交给ViewModel；ViewModel层负责将请求到的数据做业务逻辑处理，最后交给View层去展示，与View一一对应；View层只负责界面绘制刷新，不处理业务逻辑，非常适合分配独立模块开发。

- **流行框架**

	[retrofit](https://github.com/square/retrofit)+ [okhttp](https://github.com/square/okhttp)+
	[gson](https://github.com/google/gson) 负责解析json数据；  
	[coil](https://coil-kt.github.io/coil/) 负责加载图片；   
	[permissionx](https://github.com/guolindev/PermissionX) 负责Android 6.0权限申请；    
	[xpopup](https://github.com/li-xiaojun/XPopup) 多种样式Dialog框架  
	[LiveEventBus](https://github.com/JeremyLiao/LiveEventBus) LiveEventBus是一款Android消息总线，基于LiveData，具有生命周期感知能力，支持Sticky，支持AndroidX，支持跨进程，支持跨APP。  
	[BaseRecyclerViewAdapterHelper](https://github.com/CymChad/BaseRecyclerViewAdapterHelper) 大名鼎鼎的BaseRecyclerViewAdapterHelper RecyclerView适配器管理框架  
	[TabLayout](https://gitee.com/kissyourface/DslTabLayout) 一个功能强大的TabLayout框架  
	[youth.banner](https://github.com/youth5201314/banner) 一个功能强大的banner框架  
	[immersionbar](https://github.com/dz-android/ImmersionBar) 一个沉浸式管理框架  
	[TitleBar](https://github.com/getActivity/TitleBar) 公用标题栏框架  
	[SmartRefreshLayout](https://github.com/scwang90/SmartRefreshLayout) 下拉刷新框架  
	[RWidgetHelper](https://github.com/RuffianZhong/RWidgetHelper) 代替selector，各个state状态背景/边框/文字变色,不用再写大量的shape文件了  
	[ARouter](https://github.com/alibaba/ARouter) 阿里路由框架  

- **数据绑定**

	满足google目前控件支持的databinding双向绑定，并扩展原控件一些不支持的数据绑定。例如将图片的url路径绑定到ImageView控件中，在BindingAdapter方法里面则使用Glide加载图片；View的OnClick事件在BindingAdapter中方法处理防重复点击，再把事件回调到ViewModel层，实现xml与ViewModel之间数据和事件的绑定。

- **基类封装**

	专门针对MVVM模式打造的BaseActivity、BaseFragment、BaseViewModel，在View层中不再需要定义ViewDataBinding和ViewModel，直接在BaseActivity、BaseFragment上限定泛型即可使用。普通界面只需要编写Fragment，然后使用ContainerActivity盛装(代理)，这样就不需要每个界面都在AndroidManifest中注册一遍。

- **全局操作**
	1. 全局的Activity堆栈式管理，在程序任何地方可以打开、结束指定的Activity，一键退出应用程序。
	2. LoggingInterceptor全局拦截网络请求日志，打印Request和Response，格式化json、xml数据显示，方便与后台调试接口。
	3. 全局Cookie，支持SharedPreferences和内存两种管理模式。
	4. 通用的网络请求异常监听，根据不同的状态码或异常设置相应的message。
	5. 全局的异常捕获，程序发生异常时不会崩溃，可跳入异常界面重启应用。
	6. 全局事件回调，提供LiveEventBus回调方式。
	7. 全局任意位置一行代码实现文件下载进度监听（暂不支持多文件进度监听）。
    8. 全局点击事件防抖动处理，防止点击过快。


## 1、准备工作
> 网上的很多有关MVVM的资料，在此就不再阐述什么是MVVM了，不清楚的朋友可以先去了解一下。[todo-mvvm-live](https://github.com/googlesamples/android-architecture/tree/todo-mvvm-live)
### 1.1、启用databinding
在主工程app的build.gradle的android {}中加入：
```gradle
dataBinding {
    enabled true
}
```
### 1.2、依赖Library
从远程依赖：

在根目录的build.gradle中加入
```gradle
allprojects {
    repositories {
		...
        google()
        jcenter()
        maven { url 'https://jitpack.io' }
    }
}
```
在主项目app的build.gradle中依赖
```gradle
dependencies {	
    ...
    api project(':mvvmlazy')
}
```


### 1.3、配置config.gradle
如果不是远程依赖，而是下载的例子程序，那么还需要将例子程序中的config.gradle放入你的主项目根目录中，然后在根目录build.gradle的第一行加入：

```gradle
apply from: "config.gradle"
```

**注意：** config.gradle中的

android = [] 是你的开发相关版本配置，可自行修改

android_x = [] 是android_x相关配置，可自行修改

dependencies = [] 是依赖第三方库的配置，可以加新库，但不要去修改原有第三方库的版本号，不然可能会编译不过
### 1.4、配置AndroidManifest
添加权限：
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```
配置Application：


可以在你的自己AppApplication中配置

```kotlin
//是否开启日志打印
KLog.init(true);
//配置全局异常崩溃操作
CaocConfig.Builder.create()
    .backgroundMode(CaocConfig.BACKGROUND_MODE_SILENT) //背景模式,开启沉浸式
    .enabled(true) //是否启动全局异常捕获
    .showErrorDetails(true) //是否显示错误详细信息
    .showRestartButton(true) //是否显示重启按钮
    .trackActivities(true) //是否跟踪Activity
    .minTimeBetweenCrashesMs(2000) //崩溃的间隔时间(毫秒)
    .errorDrawable(R.mipmap.ic_launcher) //错误图标
    .restartActivity(LoginActivity.class) //重新启动后的activity
    //.errorActivity(YourCustomErrorActivity.class) //崩溃后的错误activity
    //.eventListener(new YourCustomEventListener()) //崩溃后的错误监听
    .apply()
```

## 2、快速上手

### 2.1、第一个Activity
> 以大家都熟悉的登录操作为例：三个文件**LoginActivty.java**、**LoginViewModel.java**、**activity_login.xml**

##### 2.1.1、关联ViewModel
在activity_login.xml中关联LoginViewModel。
```xml
<layout>
    <data>
        <variable
            type="com.rui.MvvmLazy.ui.login.LoginViewModel"
            name="viewModel"
        />
    </data>
    .....

</layout>
```

> variable - type：类的全路径 <br>variable - name：变量名

##### 2.1.2、继承BaseActivity

LoginActivty
```kotlin


class LoginActivty : BaseVmDbActivity<ActivityloginBinding, LoginViewModel>() {
    override fun initContentView(): Int {
        return R.layout.activity_login
    }

    override fun initVariableId(): Int {
        return BR.viewModel
    }

    override fun initData() {
        super.initData()
    }

    override fun initTitleBar(titleBar: TitleBar?) {
        super.initTitleBar(titleBar)
        titleBar!!.title = "登录页面"
    }
}
```
> 保存activity_login.xml后databinding会生成一个ActivityloginBinding类。（如果没有生成，试着点击Build->Clean Project）

BaseActivity是一个抽象类，有两个泛型参数，一个是ViewDataBinding，另一个是BaseViewModel，上面的ActivityLoginBinding则是继承的ViewDataBinding作为第一个泛型约束，LoginViewModel继承BaseViewModel作为第二个泛型约束。

重写BaseActivity的二个抽象方法

initContentView() 返回界面layout的id<br>
initVariableId() 返回变量的id，对应activity_login中name="viewModel"，就像一个控件的id，可以使用R.id.xxx，这里的BR跟R文件一样，由系统生成，使用BR.xxx找到这个ViewModel的id。<br>



##### 2.1.3、继承BaseViewModel

LoginViewModel继承BaseViewModel
```kotlin
class LoginViewModel : BaseViewModel() {
    override fun initData() {
        super.initData()
    }
}
```
BaseViewModel与BaseActivity通过LiveData来处理常用UI逻辑，即可在ViewModel中使用父类的showDialog()、startActivity()等方法。在这个MainViewModel中就可以尽情的写你的逻辑了！
> BaseFragment的使用和BaseActivity一样，详情参考Demo。

### 2.2、数据绑定
> 拥有databinding框架自带的双向绑定，也有扩展
##### 2.2.1、传统绑定
绑定用户名：

在ViewModel中定义
```kotlin
//用户名的绑定
var userName = MutableLiveData<String>()
```
在用户名EditText标签中绑定
```xml
android:text="@={viewModel.userName}"
```
这样一来，输入框中输入了什么，userName.get()的内容就是什么，userName.set("")设置什么，输入框中就显示什么。
**注意：** @符号后面需要加=号才能达到双向绑定效果；userName需要是public的，不然viewModel无法找到它。

点击事件绑定：

在在ViewModel中定义
```kotlin
//登录按钮的点击事件

var loginOnClick: () -> Unit = {
        ToastUtils.showShort("登录")
    }
```
在登录按钮标签中绑定
```xml
android:onClick="@{viewModel.loginOnClick}"
```
这样一来，用户的点击事件直接被回调到ViewModel层了，更好的维护了业务逻辑

这就是强大的databinding框架双向绑定的特性，不用再给控件定义id，setText()，setOnClickListener()。

**但是，光有这些，完全满足不了我们复杂业务的需求啊！MvvmLazy闪亮登场：它有一套自定义的绑定规则，可以满足大部分的场景需求，请继续往下看。**

##### 2.2.2、自定义绑定
还拿点击事件说吧，不用传统的绑定方式，使用自定义的点击事件绑定。

在LoginViewModel中定义
```kotlin
//登录按钮的点击事件
var loginOnClick: () -> Unit = {
        ToastUtils.showShort("登录")
    }
```
在activity_login中定义命名空间
```xml
xmlns:binding="http://schemas.android.com/apk/res-auto"
```
在登录按钮标签中绑定
```xml
binding:onClickCommand="@{viewModel.loginOnClick}"
```
这和原本传统的绑定不是一样吗？不，这其实是有差别的。使用这种形式的绑定，在原本事件绑定的基础之上，带有防重复点击的功能，1秒内多次点击也只会执行一次操作。如果不需要防重复点击，可以加入这条属性
```xml
binding:isThrottleFirst="@{Boolean.TRUE}"
```
那这功能是在哪里做的呢？答案在下面的代码中。
```kotlin
/**
* requireAll 是意思是是否需要绑定全部参数, false为否
* View的onClick事件绑定
* onClickCommand 绑定的命令,
* isThrottleFirst 是否开启防止过快点击
*/
   @JvmStatic
    @BindingAdapter(value = ["onClickCommand", "isThrottleFirst"], requireAll = false)
    fun onClickCommand(view: View, clickCommand: () -> Unit, isThrottleFirst: Boolean) {
        if (isThrottleFirst) {
            view.setOnClickListener { clickCommand.invoke() }
        } else {
            val mHits = LongArray(2)
            view.setOnClickListener {
                System.arraycopy(mHits, 1, mHits, 0, mHits.size - 1)
                mHits[mHits.size - 1] = SystemClock.uptimeMillis()
                if (mHits[0] < SystemClock.uptimeMillis() - 500) {
                    clickCommand.invoke()
                }
            }
        }
    }
```
onClickCommand方法是自定义的，使用@BindingAdapter注解来标明这是一个绑定方法。在方法中使用了RxView来增强view的clicks事件，.throttleFirst()限制订阅者在指定的时间内重复执行，最后通过BindingCommand将事件回调出去，就好比有一种拦截器，在点击时先做一下判断，然后再把事件沿着他原有的方向传递。

是不是觉得有点意思，好戏还在后头呢！
##### 2.2.3、自定义ImageView图片加载
绑定图片路径：

在ViewModel中定义
```kotlin
var  imgUrl = "http://img0.imgtn.bdimg.com/it/u=2183314203,562241301&fm=26&gp=0.jpg";
```
在ImageView标签中
```xml
加载普通图片
binding:bindImgUrl="@{viewModel.imgUrl}"
加载圆形图片
binding:bindCircleImgUrl="@{viewModel.imgUrl}"
加载圆角图片
binding:bindCorners="@{20}"
binding:bindCornersImgUrl="@{viewModel.imgUrl}"
```
url是图片路径，这样绑定后，这个ImageView就会去显示这张图片，不限网络图片还是本地图片。

如果需要给一个默认加载中的图片，可以加这一句
```xml
binding:placeholderRes="@{R.mipmap.ic_launcher_round}"
```
> R文件需要在data标签中导入使用，如：`<import type="com.goldze.MvvmLazy.R" />`
如果需要图片居中剪切
```xml
binding:centerCrop="@{true}"
```
BindingAdapter中的实现
```kotlin
    @JvmStatic
    @BindingAdapter(value = ["bindImgUrl", "placeholderRes", "centerCrop"], requireAll = false)
    fun bindImgUrl(imageView: ImageView, url: String?, placeholderRes: Int?, centerCrop: Boolean?) {
        var requestBuilder = Glide.with(imageView.context).asDrawable().load(url)
        if (centerCrop == null || centerCrop) {
            requestBuilder = requestBuilder.centerCrop()
        }
        requestBuilder.apply(
            RequestOptions().placeholder(
                createDefPlaceHolder(
                    imageView.context,
                    placeholderRes,
                    0f
                )
            ).override(imageView.width, imageView.height)
        )
            .into(imageView)
    }
```
很简单就自定义了一个ImageView图片加载的绑定，学会这种方式，可自定义扩展。
> 如果你对这些感兴趣，可以下载源码，在binding包中可以看到各类控件的绑定实现方式

##### 2.2.4、RecyclerView绑定
> RecyclerView也是很常用的一种控件，传统的方式需要针对各种业务要写各种Adapter，如果你使用了MvvmLazy，则可大大简化这种工作量，从此告别setAdapter()。
>使用大名鼎鼎的[BaseRecyclerViewAdapterHelper](https://github.com/CymChad/BaseRecyclerViewAdapterHelper)负责管理RecyclerView的适配器；

在ViewModel中定义：
```kotlin
//声明adapter
 var lineAdapter = object :
        DataBindingAdapter<JokeInfo, TestLayoutItemJokeBinding>(R.layout.test_layout_item_joke) {
        override fun convertItem(
            holder: BaseViewHolder,
            binding: TestLayoutItemJokeBinding?,
            item: JokeInfo
        ) {
            binding!!.entity = item
        }
    }
```
在xml中绑定
```xml
 <androidx.recyclerview.widget.RecyclerView
                bindAdapter="@{viewModel.lineAdapter}"
                layoutManager="@{LayoutManagers.linear()}"
                lineManager="@{LineManagers.divider(@color/divider,1)}"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"/>
```
layoutManager控制是线性(包含水平和垂直)排列还是网格排列，lineManager是设置分割线

水平布局的写法：`binding:layoutManager="@{LayoutManagers.linear(LinearLayoutManager.HORIZONTAL,Boolean.FALSE)}"`</br>
网格布局的写法：`binding:layoutManager="@{LayoutManagers.grid(3)}`</br>
瀑布流布局的写法：`binding:layoutManager="@{LayoutManagers.staggeredGrid(3,LayoutManagers.VERTICAL)}"</br>


使用到相关类，则需要导入该类才能使用，和导入Java类相似

> `<import type="com.rui.mvvmlazy.binding.viewadapter.recyclerview.LayoutManagers" />`</br>
> `<import type="com.rui.mvvmlazy.binding.viewadapter.recyclerview.LineManagers" />`</br>


详细可以参考例子程序中ListViewModel类。


### 2.3、网络请求
> 网络请求一直都是一个项目的核心，现在的项目基本都离不开网络，一个好用网络请求框架可以让开发事半功倍。
#### 2.3.1、Retrofit+Okhttp+RxJava3
> 现今，这三个组合基本是网络请求的标配，如果你对这三个框架不了解，建议先去查阅相关资料。

square出品的框架，用起来确实非常方便。**MvvmLazy**中引入了
```gradle
api "com.squareup.okhttp3:okhttp:4.9.1"
api "com.squareup.retrofit2:retrofit:2.9.0"
api "com.squareup.retrofit2:converter-gson:2.9.0"
api "com.squareup.retrofit2:adapter-rxjava3:2.9.0"
```
构建Retrofit时加入
```kotlin
var retrofit = Retrofit.Builder()
    .addConverterFactory(GsonConverterFactory.create())
    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
    .build();
```
或者直接使用例子程序中封装好的RetrofitClient
#### 2.3.2、网络拦截器
**LoggingInterceptor：** 全局拦截请求信息，格式化打印Request、Response，可以清晰的看到与后台接口对接的数据，
```kotlin
var mLoggingInterceptor = LoggingInterceptor
    .Builder()//构建者模式
    .loggable(true) //是否开启日志打印
    .setLevel(Level.BODY) //打印的等级
    .log(Platform.INFO) // 打印类型
    .request("Request") // request的Tag
    .response("Response")// Response的Tag
    .addHeader("version", BuildConfig.VERSION_NAME)//打印版本
    .build()
```
构建okhttp时加入
```kotlin
var okHttpClient =OkHttpClient.Builder()
    .addInterceptor(mLoggingInterceptor)
    .build()
```
**CacheInterceptor：** 缓存拦截器，当没有网络连接的时候自动读取缓存中的数据，缓存存放时间默认为3天。</br>
创建缓存对象
```kotlin
//缓存时间
var CACHE_TIMEOUT = 10 * 1024 * 1024
//缓存存放的文件
var httpCacheDirectory = new File(mContext.getCacheDir(), "goldze_cache");
//缓存对象
var cache = Cache(httpCacheDirectory, CACHE_TIMEOUT);
```
构建okhttp时加入
```kotlin
var okHttpClient =  OkHttpClient.Builder()
    .cache(cache)
    .addInterceptor(new CacheInterceptor(mContext))
    .build()
```
#### 2.3.3、Cookie管理
**MvvmLazy**提供两种CookieStore：**PersistentCookieStore** (SharedPreferences管理)和**MemoryCookieStore** (内存管理)，可以根据自己的业务需求，在构建okhttp时加入相应的cookieJar
```kotlin
var okHttpClient =  OkHttpClient.Builder()
    .cookieJar( CookieJarImpl( PersistentCookieStore(mContext)))
    .build()
```
或者
```kotlin
var okHttpClient = OkHttpClient.Builder()
    .cookieJar()
    .build()
```
#### 2.3.4、网络请求
请求在ViewModel层。默认在BaseActivity中注入了LifecycleProvider对象到ViewModel，用于绑定请求的生命周期，View与请求共存亡。
```kotlin
  request({ repository.getJoke(pageIndex, 10, "video") }, {
                  //处理请求结果
                }, {
                //处理异常情况
                })

```
#### 2.3.5、网络异常处理
网络异常在网络请求中非常常见，比如请求超时、解析错误、资源不存在、服务器内部错误等，在客户端则需要做相应的处理(当然，你可以把一部分异常甩锅给网络，比如当出现code 500时，提示：请求超时，请检查网络连接，此时偷偷将异常信息发送至后台(手动滑稽))。<br>


> MvvmLazy中自定义了一个[ExceptionHandle](./mvvmLazy/src/main/java/com.rui.mvvmlazy.http/ExceptionHandle.java)，已为你完成了大部分网络异常的判断，也可自行根据项目的具体需求调整逻辑。<br>

**注意：** 这里的网络异常code，并非是与服务端协议约定的code。网络异常可以分为两部分，一部分是协议异常，即出现code = 404、500等，属于HttpException，另一部分为请求异常，即出现：连接超时、解析错误、证书验证失等。而与服务端约定的code规则，它不属于网络异常，它是属于一种业务异常。在请求中可以使用RxJava的filter(过滤器)，也可以自定义BaseSubscriber统一处理网络请求的业务逻辑异常。由于每个公司的业务协议不一样，所以具体需要你自己来处理该类异常。
## 3、辅助功能
> 一个完整的快速开发框架，当然也少不了常用的辅助类。下面来介绍一下**MVVMabit**中有哪些辅助功能。
### 3.1、事件总线
> 事件总线存在的优点想必大家都很清楚了，android自带的广播机制对于组件间的通信而言，使用非常繁琐，通信组件彼此之间的订阅和发布的耦合也比较严重，特别是对于事件的定义，广播机制局限于序列化的类（通过Intent传递），不够灵活。
#### 3.3.1、LiveEventBus
LiveEventBus是一款Android消息总线，基于LiveData，具有生命周期感知能力，支持Sticky，支持AndroidX，支持跨进程，支持跨APP

使用方法：
```kotlin
//发送消息
LiveEventBus.get("key").post("value");
//发送一条延时消息 3秒跳转
LiveEventBus.get("key").postDelay("value",3000);

//接收消息
LiveEventBus.get("key",String.class).observe(this) {

    
}
```
更多使用方法请参考 [https://github.com/JeremyLiao/LiveEventBus](https://github.com/JeremyLiao/LiveEventBus)


### 3.2、ContainerActivity
一个盛装Fragment的一个容器(代理)Activity，普通界面只需要编写Fragment，使用此Activity盛装，这样就不需要每个界面都在AndroidManifest中注册一遍

使用方法：

在ViewModel中调用BaseViewModel的方法开一个Fragment
```kotlin
startContainerActivity(你的Fragment类名.class.getCanonicalName())
```
在ViewModel中调用BaseViewModel的方法，携带一个序列化实体打开一个Fragment
```kotlin
var mBundle = Bundle()
mBundle.putParcelable("entity", entity)
startContainerActivity(你的Fragment类名.class.getCanonicalName(), mBundle)
```
在你的Fragment中取出实体
```kotlin
var mBundle = getArguments()
if (mBundle != null) {
    entity = mBundle.getParcelable("entity")
}
```
### 3.3、6.0权限申请
> 对PermissionX已经熟悉的朋友可以跳过。

使用方法：

例如请求相机权限，在ViewModel中调用
```kotlin
  PermissionX.init(activity)
            .permissions(
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.CAMERA,
                Manifest.permission.CALL_PHONE
            )
            .onExplainRequestReason { scope, deniedList ->
                scope.showRequestReasonDialog(
                    deniedList,
                    "Core fundamental are based on these permissions",
                    "OK",
                    "Cancel"
                )
            }
            .request { allGranted, grantedList, deniedList ->
                if (allGranted) {
                    toast("All permissions are granted")
                } else {
                    toast("These permissions are denied: $deniedList")
                }
            }
```
更多权限申请方式请参考[PermissionX原项目地址](https://github.com/guolindev/PermissionX)

### 3.4、其他辅助类
**ToastUtils：** 吐司工具类

**SPUtils：** SharedPreferences工具类

**SDCardUtils：** SD卡相关工具类

**ConvertUtils：** 转换相关工具类

**StringUtils：** 字符串相关工具类

**RegexUtils：** 正则相关工具类

**KLog：** 日志打印，含json格式打印

更多工具类查看mvvmlazy下面utils目录

### 3.5、demo示例
 项目中提供了大量的demo示例,可自行下载源码查看

### 3.6、组件化方案

项目组件化方案参考了[MVVMHabitComponent] [https://github.com/goldze/MVVMHabitComponent]
## About
** 本人喜欢尝试新的技术，以后发现有好用的东西，我将会在企业项目中实战，没有问题了就会把它引入到**MvvmLazy**中，一直维护着这套框架，谢谢各位朋友的支持。如果觉得这套框架不错的话，麻烦点个 **star**，你的支持则是我前进的动力！


**邮箱**：664209769@qq.com

## License

    Copyright 2021 赵继瑞

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
