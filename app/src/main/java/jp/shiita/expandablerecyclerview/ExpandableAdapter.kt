package jp.shiita.expandablerecyclerview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import jp.shiita.expandablerecyclerview.ExpandableAdapter.ViewType.*
import kotlinx.android.synthetic.main.item_child.view.*
import kotlinx.android.synthetic.main.item_parent.view.*

class ExpandableAdapter(
        private val context: Context,
        parents: List<Parent>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val inflater = LayoutInflater.from(context)
    private val data: MutableList<Any> = parents
            .flatMap { p -> listOf(p, *p.children.toTypedArray()) }
            .toMutableList()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when(ViewType[viewType]) {
        PARENT -> ParentViewHolder(inflater.inflate(R.layout.item_parent, parent, false))
        CHILD -> ChildViewHolder(inflater.inflate(R.layout.item_child, parent, false))
        EMPTY -> EmptyViewHolder(View(context))
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ParentViewHolder -> {
                val parent = data[position] as Parent
                holder.bind(parent)
                holder.itemView.setOnClickListener {
                    parent.children.forEach { c -> c.visible = !c.visible }
                    notifyItemRangeChanged(position + 1, parent.children.size)
                }
            }
            is ChildViewHolder -> holder.bind(data[position] as Child)
        }

        // tagはDividerの有無の決定に利用
        when (holder) {
            is ParentViewHolder                    -> holder.itemView.tag = true
            is ChildViewHolder, is EmptyViewHolder -> holder.itemView.tag = false
        }
    }

    override fun getItemViewType(position: Int): Int {
        val d = data[position]
        return when(d) {
            is Parent -> PARENT.ordinal
            is Child -> if (d.visible) CHILD.ordinal else EMPTY.ordinal
            else -> error("invalid view type")
        }
    }

    enum class ViewType {
        PARENT,
        CHILD,
        EMPTY;

        companion object {
            operator fun get(ordinal: Int) = ViewType.values()[ordinal]
        }
    }

    class ParentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val title = view.title

        fun bind(parent: Parent) {
            title.text = parent.name
        }
    }

    class ChildViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val content = view.content

        fun bind(child: Child) {
            content.text = child.name
        }
    }

    class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class ExpandableDividerItemDecoration(context: Context): RecyclerView.ItemDecoration() {
        private val bounds = Rect()
        private val divider: Drawable

        init {
            val attrs = context.obtainStyledAttributes(intArrayOf(android.R.attr.listDivider))
            divider = attrs.getDrawable(0)!!
            attrs.recycle()
        }

        override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            val left = parent.paddingLeft
            val right = parent.width - parent.paddingRight

            (0 until parent.childCount).forEach { i ->
                val child = parent.getChildAt(i)
                if (child.tag as Boolean) {
                    parent.getDecoratedBoundsWithMargins(child, bounds)
                    val bottom = bounds.bottom + Math.round(child.translationY)
                    val top = bottom - divider.intrinsicHeight
                    divider.setBounds(left, top, right, bottom)
                    divider.draw(c)
                }
            }
        }

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            if ((view.tag as Boolean)) outRect.set(0, 0, 0, divider.intrinsicHeight)
        }
    }
}