/**
 * 
 */
package edu.utdallas.acngroup12.main;

import com.example.acnproject_v_1_0.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

/**
 * @author Aditya
 *
 */
public class Splash extends Activity implements AnimationListener{
	
	// Splash screen timer
    private int SPLASH_TIME_OUT = 2000;
    private int animantionCount = 0;
    private TextView textView2;
    
    //Animation
    private Animation animFadein;
	/**
	 * 
	 */
	public Splash() {
		// TODO Auto-generated constructor stub
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.splash_screen);
	    // TODO Auto-generated method stub
	    textView2 = (TextView) findViewById(R.id.textView2);
	    // load the animation
        animFadein = AnimationUtils.loadAnimation(getApplicationContext(),R.animator.fade_in);
        // set animation listener
        animFadein.setAnimationListener(this);
        textView2.startAnimation(animFadein);
    }

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		if(animantionCount < 2){
			animantionCount++;
			textView2.startAnimation(animFadein);
		}
		else{
			new Handler().postDelayed(new Runnable() {		    	 
	            /*
	             * Showing splash screen with a timer. This will be useful when you
	             * want to show case your app logo / company
	             *       */	 
	            @Override
	            public void run() {
	                // This method will be executed once the timer is over
	                // Start your app main activity
	                Intent i = new Intent(Splash.this, MainActivity.class);
	                startActivity(i); 
	                // close this activity
	                finish();
	            }
	        }, SPLASH_TIME_OUT);
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}
}
