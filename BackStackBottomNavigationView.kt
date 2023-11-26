import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MenuItem
import android.view.View
import androidx.core.view.forEach
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView

class BackStackBottomNavigationView :
    BottomNavigationView,
    BottomNavigationView.OnNavigationItemSelectedListener {
    private var customNavigationItemSelectedListener: ((item: MenuItem) -> Boolean)? =
        null
    private val menuIds: ArrayList<Int> = ArrayList()
    private var defaultId: Int = -1

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        menuIds.remove(item.itemId)
        menuIds.add(item.itemId)
        return if (customNavigationItemSelectedListener != null) {
            customNavigationItemSelectedListener!!.invoke(item)
        } else false
    }

    private val bottomMenu: BottomNavigationMenuView
        get() {
            return getChildAt(0) as BottomNavigationMenuView
        }

    fun navigateUp(): Boolean {
        if (menuIds.size > 0) {
            menuIds.removeAt(menuIds.size - 1)
            if (menuIds.size > 1 || menuIds.size == 1 && menuIds[0] != selectedItemId) {
                val prevItem = getMenuWithId(
                    menuIds[menuIds.size - 1]
                )
                if (prevItem != null && prevItem.visibility == View.GONE) {
                    navigateUp()
                } else {
                    selectedItemId = menuIds[menuIds.size - 1]
                }
                return true
            }
        }

        if (defaultId != -1 && defaultId != selectedItemId) {
            selectedItemId = defaultId
            return true
        }
        return false
    }

    fun setDefaultItemId(defaultId: Int) {
        this.defaultId = defaultId
    }

    fun setNavigationItemSelectedListener(customNavigationItemSelectedListener: ((item: MenuItem) -> Boolean)) {
        this.customNavigationItemSelectedListener = customNavigationItemSelectedListener
    }

    private fun init() {
        setOnNavigationItemSelectedListener(this)
//        bottomMenu.forEach {
//            it.findViewById<TextView>(R.id.largeLabel).setPadding(0)
//        }
    }

    private fun getMenuWithId(id: Int): View? {
        bottomMenu.forEach {
            if (it.id == id) {
                return it
            }
        }
        return null
    }

    override fun onSaveInstanceState(): Parcelable {
        val bottomNavigationData = super.onSaveInstanceState()
        return Bundle().apply {
            putParcelable(SUPER_SAVED_INSTANCE_KEY, bottomNavigationData)
            putIntegerArrayList(NAVIGATED_MENU_LIST, menuIds)
        }
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        var bottomNavigationData = state
        if (state is Bundle) {
            if (state.containsKey(NAVIGATED_MENU_LIST)) {
                menuIds.clear()
                menuIds.addAll(state.getIntegerArrayList(NAVIGATED_MENU_LIST)!!)
                if (menuIds.size > 0) {
                    selectedItemId = menuIds[menuIds.size - 1]
                }
            }
            if (state.containsKey(SUPER_SAVED_INSTANCE_KEY)) {
                bottomNavigationData = state.getParcelable(SUPER_SAVED_INSTANCE_KEY)
            }
        }
        super.onRestoreInstanceState(bottomNavigationData)
    }

    companion object {
        private const val SUPER_SAVED_INSTANCE_KEY = "SUPER_SAVED_INSTANCE_KEY"
        private const val NAVIGATED_MENU_LIST = "NAVIGATED_MENU_LIST"
    }
}
