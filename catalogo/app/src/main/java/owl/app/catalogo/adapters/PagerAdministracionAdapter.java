package owl.app.catalogo.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import owl.app.catalogo.fragments.CategoriasFragment;
import owl.app.catalogo.fragments.UsuariosFragment;
import owl.app.catalogo.fragments.VentasFragment;

public class PagerAdministracionAdapter extends FragmentStatePagerAdapter {

    private int numberOfTabs;

    public PagerAdministracionAdapter(FragmentManager fm, int numberOfTabs) {
        super(fm);
        this.numberOfTabs = numberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position)
        {
            case 0:
                return new UsuariosFragment();
            case 1:
                return new CategoriasFragment();
            case 2:
                return new VentasFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
