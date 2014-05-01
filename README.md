SuperToolTips ([Play Store Demo][1]) [![Build Status](https://travis-ci.org/nhaarman/supertooltips.svg?branch=master)](https://travis-ci.org/nhaarman/supertooltips)
===========

SuperToolTips is an Open Source Android library that allows developers to easily create Tool Tips for views.
Feel free to use it all you want in your Android apps provided that you cite this project and include the license in your app.

Setup
-----
*Note: SuperToolTips now uses the gradle build structure. If you want to use this project in Eclipse, you should make the necessary changes.*

Add the following to your `build.gradle`:


```groovy
dependencies {
    compile 'com.nhaarman.supertooltips:library:3.0.+'
}

```
Usage
-----

* In your layout xml file, add the `ToolTipRelativeLayout` (`com.nhaarman.supertooltips.ToolTipRelativeLayout`) with height and width of `match_parent`. Make sure this view is on top!
* Find the `ToolTipRelativeLayout` in your code, and start adding `ToolTips`!

Example:
-----
```java
<RelativeLayout
	xmlns:android="http://schemas.android.com/apk/res/android"
	android:layout_width="match_parent"
	android:layout_height="match_parent">

	<TextView
	    android:id="@+id/activity_main_redtv"
	    android:layout_width="wrap_content"
	    android:layout_height="wrap_content"
	    android:layout_centerInParent="true" />

	<com.nhaarman.supertooltips.ToolTipRelativeLayout
		android:id="@+id/activity_main_tooltipRelativeLayout"
		android:layout_width="match_parent"
		android:layout_height="match_parent" />
</RelativeLayout>

@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);
	
	ToolTipRelativeLayout toolTipRelativeLayout = (ToolTipRelativeLayout) findViewById(R.id.activity_main_tooltipRelativeLayout);
		
	ToolTip toolTip = new ToolTip()
	                    .withText("A beautiful View")
	                    .withColor(Color.RED)
	                    .withShadow(true)
						.withAnimationType(ToolTip.ANIMATIONTYPE_FROMTOP);
	myToolTipView = toolTipRelativeLayout.showToolTipForView(toolTip, findViewById(R.id.activity_main_redtv));
	myToolTipView.setOnToolTipViewClickedListener(MainActivity.this);
}
```
	

ToolTip customization
-----
You can customize the `ToolTip` in several ways:

* Specify a content text using `ToolTip.setText()`.
* Set a color using `ToolTip.setColor()`.
* Specify whether to show a shadow or not with `ToolTip.setShadow()`.
* Specify how to animate the ToolTip: from the view itself or from the top, using `ToolTip.setAnimationType()`.
* Set your own custom content View using `ToolTip.setContentView()`.

See the examples.

Developed By
-----
* Niek Haarman

License
-----

	Copyright 2013 Niek Haarman

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.

 [1]: https://play.google.com/store/apps/details?id=com.haarman.supertooltips
