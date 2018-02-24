# JsWebViewInteraction

关于webview优化的一些方案：

首次加载页面的加载速度优化
原生端资源预加载
在应用启动时就初始化一个 WebView待用，并隐藏 ，事先加载常用H5页面资源，在初始化的同时，进行网络请求，通过Native来完成一些网络请求，可以先从本地离线缓存获取页面，获取不到再从CDN和offlineServer获取页面，从服务端拿数据，当页面初始化完成后，向native获取请求的数据
具体实现：比如Android ,在BaseApplication就初始化一个WebView对象用于加载常用的H5页面资源；当需要使用这些页面时再从BaseApplication里取过来直接使用 ，对于Android WebView的首页使用这种方案，能有效提高首页加载的效率。
对于一些通用资源文件可存放在本地，在请求网页里拦截请求，如果是这些资源直接使用已有资源，如果需要更新放到本地的静态资源，有两种方式，1，发布新版本安装更新，
2,增量更新：在用户处于WIFI环境时让服务器推送到本地 。
从webview里设置加载页面完成后再加载图片

服务端建立连接/服务器处理优化
这方面的耗时一般是：DNS、connection、服务器处理
首先应用cdn加速，DNS采用和客户端API相同的域名 ，复用缓存域名和链接 
服务器采用chunked输出，并优先将页面的静态部分输出，并最终返回页面，可以让后端的API请求和前端的资源加载同时进行，在后端计算的同时前端也加载网络静态资源。

web前端优化
CSS不会阻塞HTML的解析，但如果CSS后面有JS，则会阻塞JS的执行直到CSS加载完成（即便JS是内联的脚本），从而间接阻塞HTML的解析。要求Css放在顶部，HTML放中间，JS放在底部，如果必须要在头部增加内联脚本，一定要放在CSS标签之前。
对html、css、js进行压缩，避免使用CSS表达式，尽量使用外部JS和Css，避免使用重定向
，可缓存的AJAX就尽量缓存，S代码的编译和执行会有缓存，同App中网页尽量统一框架。

二次加载页面的加载速度优化
web前端优化缓存，对于静态文件，如 JS、CSS、字体、图片等，适合通过浏览器缓存机制或app cache来进行缓存，对于 Web 在本地或服务器获取的数据，可以通过 Dom Storage 和 IndexedDB 进行缓存
对于浏览器缓存机制在要缓存的资源文件名中加上版本号或文件 MD5值，如 common.d5d02a02.js，common.v1.js，同时设置 Cache-Control:max-age=31536000，也就是一年。在一年时间内，资源文件如果本地有缓存，就会使用缓存，不会向服务端询问是否更新的请求。 
如果资源文件有修改后，同时修改资源文件名，如 common.v2.js，html页面也会引用新的资源文件名。webview需要打开对各存储机制的支持仿原生体验。

错误页面使用原生
在android方面在WebViewClient的onReceivedError 方法里执行loadDataWithBaseURL(null, "", "text/html", "utf-8", null)清除掉默认错误页内容，再显示原生错误界面。

模仿原生页面滑动到底部自动加载更多的功能
利用webview的滚动监听，页面底部距离滚动条底部的高度差<=一个设定的阈值时进行请求更多数据
在WebView中，click通常会有大约300ms的延迟（同时包括链接的点击，表单的提交，控件的交互等任何用户点击行为）。前端使用fastclick框架解决这个问题
扩展
如果要在网页不能修改代码的基础上，让网页调用原生能力，可以原生向html写入js方法，在JS方法里调用原生方法。
Android端需要在在网页加载完成后的onPageFinished方法里写入JS方法。

Android js调用原生方法
1，通过android添加js接口类
2，通过js执行confirm方法，Android WebChromeClient类里的jsConfirm方法监听消息（Android4.2版本之前，通过android提供的添加JS接口类会有安全漏洞）
性能注意
Android方面
请使用 runOnUiThread()方法来保证你关于WebView的操作是在UI线程中进行的，Android 4.4以上提供了 evaluateJavascript() 专门来异步执行JavaScript代码
