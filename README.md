
# FridgeMagnet

A library that makes it very simple to create a window icon, similar to Facebook's chat icon, and also similar to the [Link Bubble](https://play.google.com/store/apps/details?id=com.linkbubble.playstore&hl=en) app.
See the Demo project for sample implementations.

## Usage (gradle)

## How to create a FridgeMagnet

### Extend the RefrigeratorService

``` java
public class MyService extends RefrigeratorService {

...

}

```

### Use the required permission, and add the service in your AndroidManifest.xml

``` xml
...
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
...
<service android:name=".MyService"/>
...
```

### Implement the FridgeMagnetRequirements

This interface provides the paramters of your fridge magnet

``` java
    @Override
    protected FridgeMagnetRequirements getIcon() {
        return new FridgeMagnetRequirements() {
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
            public boolean removeIconShouldBeResponsive() {
                return true; // whether the remove icon responds to your touches
            }

            @Override
            public boolean shouldStickToWall() {
                return true; // whether your fridgemagnet sticks to the edge of your screen
            }

            @Override
            public boolean shouldFlingAway() {
                return true; // whether you can fling away your FridgeMagnet
            }
        };
    }
```

## API Requirements
The minimum supported Android version is IceCreamSandwich - android 4.0 (API Level 14)

## License
This software is distributed under Apache License 2.0:
http://www.apache.org/licenses/LICENSE-2.0

---

> Author
> [Prem Nirmal](https://twitter.com/premnirmal88)
