package com.xixiaohui.reader

import android.app.Activity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlin.random.Random.Default.Companion


/** Main Activity. Inflates main activity xml.  */
class RewardedActivity : Activity() {
    private var coinCount = 0
    private var coinCountText: TextView? = null
    private var countDownTimer: CountDownTimer? = null
    private var gameOver = false
    private var gamePaused = false
    private var rewardedAd: RewardedAd? = null
    private var retryButton: Button? = null
    private var showVideoButton: Button? = null
    private var timeRemaining: Long = 0
    var isLoading = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MobileAds.initialize(
            this
        ) { }
        loadRewardedAd()

        // Create the "retry" button, which tries to show a rewarded ad between game plays.
        retryButton = findViewById(R.id.retry_button)
        retryButton!!.setVisibility(View.INVISIBLE)
        retryButton!!.setOnClickListener(
            View.OnClickListener { startGame() })

        // Create the "show" button, which shows a rewarded video if one is loaded.
        showVideoButton = findViewById(R.id.show_video_button)
        showVideoButton!!.setVisibility(View.INVISIBLE)
        showVideoButton!!.setOnClickListener(
            View.OnClickListener { showRewardedVideo() })

        // Display current coin count to user.
        coinCountText = findViewById(R.id.coin_count_text)
        coinCount = 0
        coinCountText!!.setText("Coins: $coinCount")
        startGame()
    }

    public override fun onPause() {
        super.onPause()
        pauseGame()
    }

    public override fun onResume() {
        super.onResume()
        if (!gameOver && gamePaused) {
            resumeGame()
        }
    }

    private fun pauseGame() {
        countDownTimer!!.cancel()
        gamePaused = true
    }

    private fun resumeGame() {
        createTimer(timeRemaining)
        gamePaused = false
    }

    private fun loadRewardedAd() {
        if (rewardedAd == null || !rewardedAd!!.isLoaded) {
            rewardedAd = RewardedAd(this, RewardedActivity.AD_UNIT_ID)
            isLoading = true
            rewardedAd!!.loadAd(
                AdRequest.Builder().build(),
                object : RewardedAdLoadCallback() {
                    override fun onRewardedAdLoaded() {
                        // Ad successfully loaded.
                        isLoading = false
                        Toast.makeText(this@RewardedActivity, "onRewardedAdLoaded", Toast.LENGTH_SHORT)
                            .show()
                    }

                    override fun onRewardedAdFailedToLoad(loadAdError: LoadAdError) {
                        // Ad failed to load.
                        isLoading = false
                        Toast.makeText(
                            this@RewardedActivity,
                            "onRewardedAdFailedToLoad",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                })
        }
    }

    private fun addCoins(coins: Int) {
        coinCount += coins
        coinCountText!!.text = "Coins: $coinCount"
    }

    private fun startGame() {
        // Hide the retry button, load the ad, and start the timer.
        retryButton!!.visibility = View.INVISIBLE
        showVideoButton!!.visibility = View.INVISIBLE
        if (!rewardedAd!!.isLoaded && !isLoading) {
            loadRewardedAd()
        }
        createTimer(RewardedActivity.COUNTER_TIME)
        gamePaused = false
        gameOver = false
    }

    // Create the game timer, which counts down to the end of the level
    // and shows the "retry" button.
    private fun createTimer(time: Long) {
        val textView = findViewById<TextView>(R.id.timer)
        if (countDownTimer != null) {
            countDownTimer!!.cancel()
        }
        countDownTimer = object : CountDownTimer(time * 1000, 50) {
            override fun onTick(millisUnitFinished: Long) {
                timeRemaining = millisUnitFinished / 1000 + 1
                textView.text = "seconds remaining: $timeRemaining"
            }

            override fun onFinish() {
                if (rewardedAd!!.isLoaded) {
                    showVideoButton!!.visibility = View.VISIBLE
                }
                textView.text = "You Lose!"
                addCoins(RewardedActivity.GAME_OVER_REWARD)
                retryButton!!.visibility = View.VISIBLE
                gameOver = true
            }
        }
        countDownTimer!!.start()
    }

    private fun showRewardedVideo() {
        showVideoButton!!.visibility = View.INVISIBLE
        if (rewardedAd!!.isLoaded) {
            val adCallback: RewardedAdCallback = object : RewardedAdCallback() {
                override fun onRewardedAdOpened() {
                    // Ad opened.
                    Toast.makeText(this@RewardedActivity, "onRewardedAdOpened", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onRewardedAdClosed() {
                    // Ad closed.
                    Toast.makeText(this@RewardedActivity, "onRewardedAdClosed", Toast.LENGTH_SHORT)
                        .show()
                    // Preload the next video ad.
                    loadRewardedAd()
                }

                override fun onUserEarnedReward(rewardItem: RewardItem) {
                    // User earned reward.
                    Toast.makeText(this@RewardedActivity, "onUserEarnedReward", Toast.LENGTH_SHORT)
                        .show()
                    addCoins(rewardItem.amount)
                }

                override fun onRewardedAdFailedToShow(adError: AdError) {
                    // Ad failed to display
                    Toast.makeText(
                        this@RewardedActivity,
                        "onRewardedAdFailedToShow",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
            rewardedAd!!.show(this, adCallback)
        }
    }

    companion object {
        private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
        private const val COUNTER_TIME: Long = 10
        private const val GAME_OVER_REWARD = 1
    }
}