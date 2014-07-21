
# Magnet

Now you can create your own *Magnet magnet* for your android phone!

This library enables you to create a window icon similar to Facebooks chat icon, and also similar to the [Link Bubble](https://play.google.com/store/apps/details?id=com.linkbubble.playstore&hl=en) app.
See the demo project for sample implementations.

The library takes care of all the touching and dragging of the window icon, leaving you with callbacks so you can save your time
doing the important stuff.

## Usage (gradle)

### Locally
Add the following in your build.gradle and settings.gradle

``` groovy
compile project(:Libraries:Magnet)
```

``` groovy
include ':Libraries:Magnet'
```

### Via maven central
Coming soon

---

## How to create a Magnet

### Extend the MagnetService

``` java
public class MyService extends MagnetService {

...

}

```

### Use the required permission, and add the service in your AndroidManifest.xml

``` xml
...
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
...
<application>
...
    <service android:name=".MyService"/>
...
</application>
```

### Implement the MagnetRequirements

This interface provides the paramters of your  magnet

``` java
@Override
protected MagnetRequirements getIcon() {
    return new MagnetRequirements() {
        @Override
        public View getIconView(Context context) {
            ImageView icon = new ImageView(context); // your icon view can be any view
            icon.setImageResource(R.drawable.ic_launcher);
            return icon;
        }

        @Override
        public int getRemoveIconResID() {
            return -1; // you can set a custom remove icon or use the default one
        }

        @Override
        public int getShadowBackgroundResID() {
            return -1; // set the shadow background, or -1 if you want the default one
        }

        @Override
        public boolean removeIconShouldBeResponsive() {
            return true; // whether the remove icon responds to your touches
        }

        @Override
        public boolean shouldStickToWall() {
            return true; // whether your magnet sticks to the edge of your screen when you release it
        }

        @Override
        public boolean shouldFlingAway() {
            return true; // whether you can fling away your Magnet towards the bottom of the screen
        }
    };
}
```

---

## API Requirements

The minimum supported Android version is API Level 3

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
