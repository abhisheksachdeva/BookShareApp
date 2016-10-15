[1mdiff --git a/app/src/main/java/com/sdsmdg/bookshareapp/BSA/ui/MainActivity.java b/app/src/main/java/com/sdsmdg/bookshareapp/BSA/ui/MainActivity.java[m
[1mindex 3f60c69..7ca7f9a 100644[m
[1m--- a/app/src/main/java/com/sdsmdg/bookshareapp/BSA/ui/MainActivity.java[m
[1m+++ b/app/src/main/java/com/sdsmdg/bookshareapp/BSA/ui/MainActivity.java[m
[36m@@ -464,7 +464,7 @@[m [mpublic class MainActivity extends AppCompatActivity implements NavigationView.On[m
     public void onBackPressed() {[m
 [m
         if (this.drawerLayout.isDrawerVisible(GravityCompat.START)) {[m
[31m-            [m
[32m+[m
             this.drawerLayout.closeDrawer(GravityCompat.START);[m
 [m
         }else{[m
