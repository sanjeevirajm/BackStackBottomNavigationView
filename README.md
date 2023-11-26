# BackStackBottomNavigationView
BackStackBottomNavigationView - stores tab navigation order and handles it when pressing back

override fun onBackPressed() = with(viewBinding) {
    if (!backStackBottomNavigationView.navigateUp()) {
        super.onBackPressed()
    }        
}
