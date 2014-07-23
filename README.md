# Magnet

This library enables you to create a window icon similar to Facebooks chat icon, and also similar to the [Link Bubble](https://play.google.com/store/apps/details?id=com.linkbubble.playstore&hl=en) app.
See the demo project for sample implementations.

The library takes care of all the touching and dragging of the window icon, leaving you with callbacks so you can save your time
doing the important stuff.

![] (img/magnet.gif)

## Usage (gradle)

#### Via maven central

``` xml
<dependency>
  <groupId>com.premnirmal.magnet</groupId>
  <artifactId>library</artifactId>
  <version>1.1.1</version>
  <type>aar</type>
</dependency>
```

#### Locally

Add the following in your build.gradle and settings.gradle

**`build.gradle`**

``` groovy
compile project(:Libraries:Magnet)
```

**`settings.gradle`**

``` groovy
include ':Libraries:Magnet'
```

---

## How to create a Magnet

### Use the required permission, and add the service in your AndroidManifest.xml

``` xml
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
```

### Use the magnet builder in your Activity or Service

``` java

ImageView iconView = new ImageView(this);
iconView.setImageResource(R.drawable.ic_launcher);
        mMagnet = new Magnet.Builder(this)
        .setIconView(iconView)
        .setIconCallback(this)
        .setRemoveIconResId(R.drawable.trash)
        .setRemoveIconShadow(R.drawable.bottom_shadow)
        .setShouldFlingAway(true)
        .setShouldStickToWall(true)
        .setRemoveIconShouldBeResponsive(true)
        .build();
        mMagnet.show();


        ...

        mMagnet.destroy(); // to remove the magnet
```



### Use the callbacks per your needs

``` java
    @Override
    public void onFlingAway() {
        Log.i(TAG, "onFlingAway");
    }

    @Override
    public void onMove(float x, float y) {
        Log.i(TAG, "onMove(" + x + "," + y + ")");
    }

    @Override
    public void onIconClick(View icon, float iconXPose, float iconYPose) {
        Log.i(TAG, "onIconClick(..)");
    }

    @Override
    public void onIconDestroyed() {
        Log.i(TAG, "onIconDestroyed()");
    }
```

---

## API Requirements

- The minimum supported Android version is Android 1.5 Cupcake (API Level 3)
- Requires the permission `android.permission.SYSTEM_ALERT_WINDOW`

## License

The MIT License (MIT)

Copyright (c) 2014 Prem Nirmal

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.

---

> Author
> [Prem Nirmal](https://twitter.com/premnirmal88)
