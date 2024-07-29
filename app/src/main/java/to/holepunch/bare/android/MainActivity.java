package to.holepunch.bare.android;

// Import required android classes
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

// Import Bare Kit
import to.holepunch.bare.kit.IPC;
import to.holepunch.bare.kit.RPC;
import to.holepunch.bare.kit.Worklet;

public class MainActivity extends Activity {
  static {
    System.loadLibrary("bare_android");
  }

  Worklet worklet;
  IPC ipc;
  RPC rpc;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Create a LinearLayout
    LinearLayout linearLayout = new LinearLayout(this);
    linearLayout.setOrientation(LinearLayout.VERTICAL);
    linearLayout.setGravity(Gravity.CENTER);

    // Create an ImageView
    ImageView logoView = new ImageView(this);
    try {
      InputStream is = getAssets().open("Logo.png");
      Bitmap bitmap = BitmapFactory.decodeStream(is);
      logoView.setImageBitmap(bitmap);
      // Set the layout parameters to make the logo bigger
      LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
          800, // Width in pixels
          600  // Height in pixels
      );
      logoView.setLayoutParams(layoutParams);
      logoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
    } catch (IOException e) {
      e.printStackTrace();
    }

    // Create a TextView
    TextView textView = new TextView(this);
    textView.setText("Waiting for clipboard..");
    textView.setTextSize(24);
    textView.setGravity(Gravity.CENTER);

    // Create a Button
    Button copyButton = new Button(this);
    copyButton.setText("Copy text");

    // Add the ImageView, TextView, and Button to the LinearLayout
    linearLayout.addView(logoView);
    linearLayout.addView(textView);
    linearLayout.addView(copyButton);

    // Set the LinearLayout as the content view of the activity
    setContentView(linearLayout);

    worklet = new Worklet();

    try {
      worklet.start("/app.bundle", getAssets().open("app.bundle"));
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    // Setup RPC with js/app.js
    ipc = new IPC(worklet);
    rpc = new RPC(ipc, (req, error) -> {
      // Check if a "ping" request is received.
      if (req.command.equals("ping")) {
        String data = req.data("UTF-8");
        // Log data to Logcat
        Log.i("bare", req.data("UTF-8"));
        // Dynamically change text
        textView.setText(data);
      }
    });
  }

  @Override
  public void onPause() {
    super.onPause();

    worklet.suspend();
  }

  @Override
  public void onResume() {
    super.onResume();

    worklet.resume();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();

    try {
      ipc.close();
      ipc = null;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    worklet.terminate();
    worklet = null;
  }
}
