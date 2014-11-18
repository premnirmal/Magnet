[![Build Status](https://travis-ci.org/premnirmal/Magnet.svg?branch=master)](https://travis-ci.org/premnirmal/Magnet)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.premnirmal.magnet/library/badge.png)](http://search.maven.org/#artifactdetails|com.premnirmal.magnet|library|1.1.1|)
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Magnet-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/1139)

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
compile project(':Libraries:Magnet')
```

**`settings.gradle`**

``` groovy
include ':Libraries:Magnet'
```

---

## How to create a Magnet

### Use the required permission

``` xml
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
```

### Use the magnet builder in your Activity or Service

``` java

ImageView iconView = new ImageView(this);
iconView.setImageResource(R.drawable.ic_launcher);
mMagnet = new Magnet.Builder(this)
        .setIconView(iconView) // required
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

## Contributing

Please fork this repository and contribute back using [pull requests](https://github.com/premnirmal/Magnet/pulls).

## License

MIT

---

> Author
> Prem Nirmal | [illegalstate.com](http://illegalstate.com/) | [twitter](https://twitter.com/premnirmal88)
