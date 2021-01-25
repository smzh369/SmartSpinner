# Smart Spinner [![](https://jitpack.io/v/com.gitee.Zerlings/SmartSpinner.svg)](https://jitpack.io/#com.gitee.Zerlings/SmartSpinner)  

SmartSpinner是一款灵活的弹出菜单控件，相比于原生的Spinner，SmartSpinner在保留易用性的同时，具有高度的可定制性，能满足更多UI上的需求。

控件包含两个版本，简单易用的基础版SmartSpinner，和支持自定义布局的进阶版SmartSpinnerLayout。

0.2.0以上版本仅支持AndroidX

Requirement
-----------
Kotlin / Java(需开启kotlin插件支持)

Android API 17+

Usage
-------
### 1.SmartSpinner
基础版的使用很简单:
```xml
 <com.zerlings.spinner.SmartSpinner
        android:id="@+id/spinner"
        android:layout_width="100dp"
        android:layout_height="wrap_content"
        android:paddingStart="10dp"
        app:menuPaddingEnd="5dp"
        app:menuPaddingStart="10dp"
        app:textAlignment="start"
        app:textSize="16sp"
        app:entries="@array/languages"
        app:showSelectedColor="true"
        app:presetIndex="2"
        app:menuWidth="fit_horizon"
        app:arrowPadding="0dp"/>
```
* Note: `menuWidth`可以是固定值/match_parent/wrap_content/fit_horizon，其中fit_horizion表示弹出菜单与spinner宽度一致。

 弹出菜单的数据也可以在代码中设置:
```kotlin
 spinner.setDataSource(arrayListOf("apple", "banana", "orange"))
```

#### Listeners
支持两种监听器。除了常规的选择监听外，还提供一个重置监听:
```kotlin
//Kotlin
spinner.setOnItemSelectedListener { view, index ->
            //todo
        }
spinner.setOnSpinnerResetListener {
            //todo
        }

//Java
spinner.setOnItemSelectedListener(new Function2<View, Integer, Unit>() {
            @Override
            public Unit invoke(View view, Integer integer) {
                //todo
                return null;
            }
        });
spinner.setOnSpinnerResetListener(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                //todo
                return null;
            }
        });

//Java with lambda
spinner.setOnItemSelectedListener((view, i) -> {
            //todo
            return null;
        });
spinner.setOnSpinnerResetListener(() -> {
            //todo
            return null;
        });
```
* Note: 在Java中使用需要返回一个任意值

#### Attributes
基础版有丰富的自定义属性:

| name                      | type      | info                                                   |
|------------------------   |-----------|--------------------------------------------------------|
| arrowTint                 | color     | 小箭头颜色                  |
| hideArrow                 | boolean   | 是否显示小箭头           |
| arrowDrawable             | reference | 小箭头资源文件                |
| arrowPadding             | dimension | 小箭头与文本间距                |
| textAlignment             | enum | 文本对齐方式                               |
| menuPaddingStart               | dimension | 弹出菜单左内边距          |
| menuPaddingStart               | dimension | 弹出菜单右内边距          |
| menuWidth        | dimension/enum | 弹出菜单宽度 |
| menuOffsetX        | dimension  | 菜单弹出位置横向偏移量 |
| menuOffsetY        | dimension  | 菜单弹出位置纵向偏移量 |
| textColor        | color | 文字颜色 |
| selectedColor        | color | 选中item文字颜色 |
| textSize        | color | 文字大小 |
| presetIndex        | integer | 默认选中item |
| presetText        | string/reference | 默认文本，设置该项后presetIndex固定为-1，即初始化后不选中任何一栏 |
| presetIndex        | color | 默认选中item |
| spinnerBackground        | color/reference | spinner背景色 |
| menuBackground        | color/reference | 弹出菜单背景色 |
| selectedBackground      | color/reference | 弹出菜单被选中item的背景色 |
| showSelectedColor       | boolean | 若此项为true，当选中item与预设不同时，spinner文本颜色会随item改变 |
| entries                   | reference | 数据源，定义在<string-array/>标签下的字符串数组，用法与官方spinner相同 |
| showDivider      | boolean | 下拉菜单显示分隔线 |
| dividerColor       | color | 分隔线颜色 |
| dividerPadding          | dimension | 分隔线左右边距 |
| dividerHeight          | dimension | 分隔线厚度 |

### 2.SmartSpinnerLayout
进阶版首先仍是在xml中定义：
```xml
<com.zerlings.spinner.SmartSpinnerLayout
        android:id="@+id/spinner_layout"
        android:layout_width="200dp"
        android:layout_height="50dp"
        app:layoutMenuWidth="match_parent"
        android:background="@color/colorPrimaryDark">

        <TextView
            android:id="@+id/spinner_text"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textSize="15sp"
            android:text="fruit"
            app:layout_constraintLeft_toLeftOf="parent"
            app:layout_constraintRight_toRightOf="parent"
            app:layout_constraintTop_toTopOf="parent"
            app:layout_constraintBottom_toBottomOf="parent"/>

</com.zerlings.spinner.SmartSpinnerLayout>
```
* Note: SmartSpinnerLayout可以看作是一个ConstraintLayout，内部可包含任意自定义布局，而弹出菜单item布局文件写法与RecyclerView相同。

然后自定义Adapter继承`BaseSpinnerLayoutAdapter<T>`，实现`onbind()`和`onRefresh()`两个方法并传入adapter的布局id：
```kotlin
class SmartSpinnerLayoutAdapter(dataList: MutableList<PayType>) : BaseSpinnerLayoutAdapter<PayType>(R.layout.item_spinner_layout, dataList) {

    //相当于RecyclerView.Adapter的onBindViewHolder
    override fun onBind(holder: BaseViewHolder, position: Int) {
        holder.itemView.apply {
            item_text.text = dataList[position].title
            item_icon.setImageResource(dataList[position].imgResId)
            if (position == selectedPosition){
                item_hook.visibility = View.VISIBLE
                item_text.setTextColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
                setBackgroundResource(R.color.colorAccent)
            }else {
                item_hook.visibility = View.GONE
                item_text.setTextColor(Color.BLACK)
                setBackgroundResource(R.color.light_gray)
            }
        }
    }

    //选中menuItem后刷新SmartSpinnerLayout里的控件，position为选中item的序号
    //初始化setAdapter()和调用reset()方法时该方法也会调用，此时position为设置的presetIndex（默认-1），可能需要特殊处理
    override fun onRefresh(view: SmartSpinnerLayout<*>, position: Int) {
        view.spinner_text.text = if (position == -1) "paytype" else dataList[position].title
        view.spinner_text.setTextColor(if (position == presetPosition) Color.BLACK else ResourcesCompat.getColor(view.resources, R.color.colorPrimary, null))
    }
}
```
最后在代码中初始化：
```kotlin
val spinnerLayout: SmartSpinnerLayout<PayType> = findViewById(R.id.spinner_layout)
val adapter = SmartSpinnerLayoutAdapter(arrayListOf(PayType("apple", R.mipmap.wechat_icon),
            PayType("banana", R.mipmap.withdraw_alipay),
            PayType("orange", R.mipmap.withdraw_unipay)))
spinnerLayout.setAdapter(adapter)
```
* Note: Java中使用方法相似，此处省略相关代码

#### Listeners
监听器用法与基础版相同：
```
//Kotlin
spinnerLayout.setOnItemSelectedListener { view, index ->
            //todo
        }
spinnerLayout.setOnSpinnerResetListener {
            //todo
        }

//Java
spinner.setOnItemSelectedListener(new Function2<View, Integer, Unit>() {
            @Override
            public Unit invoke(View view, Integer integer) {
                //todo
                return null;
            }
        });
spinner.setOnSpinnerResetListener(new Function0<Unit>() {
            @Override
            public Unit invoke() {
                //todo
                return null;
            }
        });

//Java with lambda
spinner.setOnItemSelectedListener((view, i) -> {
            //todo
            return null;
        });
spinner.setOnSpinnerResetListener(() -> {
            //todo
            return null;
        });
```

#### Attributes
进阶版的xml属性相对较少，因为大部分都可以通过布局文件定制：
| name                      | type      | info                                                   |
|------------------------   |-----------|--------------------------------------------------------|
| layoutMenuWidth        | dimension/enum | 弹出菜单宽度 |
| layoutMenuOffsetX        | dimension  | 菜单弹出位置横向偏移量 |
| layoutMenuOffsetY        | dimension  | 菜单弹出位置纵向偏移量 |
| layoutPresetIndex        | integer | 默认选中位置 |
| showDivider      | boolean | 下拉菜单显示分隔线 |
| dividerColor       | color | 分隔线颜色 |
| dividerPadding          | dimension | 分隔线左右边距 |
| dividerHeight          | dimension | 分隔线厚度 |

Include
-------
首先在项目根目录下的`build.gradle`中加入（已有则忽略）:
```groovy
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```
然后在app文件夹下的`build.gradle`中引入：
```
dependencies {
    implementation 'com.gitee.Zerlings:SmartSpinner:0.2.2'
}
```

License
-------
    Copyright (C) 2020 Zerlings

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.