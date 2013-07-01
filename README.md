SuperToolTips ([Play Store Demo][1])
===========

ListViewAnimations is an Open Source Android library that allows developers to easily create ToolTips for views.
Feel free to use it all you want in your Android apps provided that you cite this project and include the license in your app.

For now, API11+ is supported, in the very near future, I will add support for API8+.

Setup
-----
* In Eclipse, just import the library as an Android library project.
* Project > Clean to generate the binaries you need, like R.java, etc.
* Then, just add ListViewAnimations as a dependency to your existing project and you're good to go!

Usage
-----

* In your layout xml file, add the `ToolTipFrameLayout` (`com.haarman.supertoasts.ToolTipFrameLayout`) with height and width of `match_parent`. Make sure this view is on top!
* Find the `ToolTipFrameLayout` in your code, and start adding `ToolTips`!

Example:
-----

	<RelativeLayout
		xmlns:android="http://schemas.android.com/apk/res/android"
		xmlns:tools="http://schemas.android.com/tools"
		android:layout_width="match_parent"
		android:layout_height="match_parent"	
		tools:context=".MainActivity">

		<!-- Rest of Layout -->

		<com.haarman.supertoasts.ToolTipFrameLayout
			android:id="@+id/activity_main_tooltipframelayout"
			android:layout_width="match_parent"
			android:layout_height="match_parent" />
	</RelativeLayout>

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ToolTipFrameLayout toolTipFrameLayout = (ToolTipFrameLayout) findViewById(R.id.activity_main_tooltipframelayout);
		
		myToolTipView = toolTipFrameLayout.showToolTipForView(
                new ToolTip()
                        .withText("A beautiful View")
                        .withColor(Color.RED)
                        .withShadow(true)
						.withAnimationType(ToolTip.ANIMATIONTYPE_FROMTOP),
                findViewById(R.id.activity_main_redtv));
        myToolTipView.setOnToolTipViewClickedListener(MainActivity.this);
	}
	
	

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