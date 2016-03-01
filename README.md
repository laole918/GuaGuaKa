### 刮刮卡简介
Android 自定义控件实现刮刮卡效果

****

### 使用Gradle构建时添加一下依赖即可
```groovy
    maven {
        url 'https://dl.bintray.com/laole918/maven/'
    }
```
```groovy
    compile 'com.laole918:guaguaka:1.0.0'
```
### 示例
```xml
    <com.laole918.guaguaka.widget.GuaGuaKaFrameLayout 
        xmlns:ggk="http://schemas.android.com/apk/res-auto"
        android:id="@+id/ggk_frameLayout"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="#3ff3ff"
        ggk:ggk_foreground="#dddddd"
        ggk:ggk_strokeWidth="20dp">

        <LinearLayout
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_gravity="center"
            android:layout_marginLeft="15dp"
            android:layout_marginRight="15dp"
            android:layout_marginBottom="15dp"
            android:layout_marginTop="15dp"
            android:orientation="vertical">

            <TextView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="恭喜你中了100000000万，快去领奖吧" />

            <Button
                android:id="@+id/btn_view_desc"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:text="查看详情" />
        </LinearLayout>
    </com.laole918.guaguaka.widget.GuaGuaKaFrameLayout>
```
### 效果
![](https://github.com/laole918/GuaGuaKa/raw/master/preview/device-2016-02-29-170906.gif)
### 感谢
此库是在看过[张鸿洋的博客](http://blog.csdn.net/lmj623565791/article/details/40162163 "张鸿洋的博客")以及[张大神的视频](http://www.imooc.com/learn/225 "慕客网")之后稍作修改而成。
自己的原创的东西很少。