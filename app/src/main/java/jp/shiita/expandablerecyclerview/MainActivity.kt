package jp.shiita.expandablerecyclerview

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addItemDecoration(ExpandableAdapter.ExpandableDividerItemDecoration(this))
        val random = Random()
        recyclerView.adapter = ExpandableAdapter(this, (1..20).map { p ->
            Parent("parent$p",
                    (1..(3 + random.nextInt(5))).map { c -> Child("child$p-$c") })
        })
    }
}
