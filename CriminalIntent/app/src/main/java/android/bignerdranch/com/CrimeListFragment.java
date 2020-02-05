package android.bignerdranch.com;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CrimeListFragment extends Fragment {
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;

    private boolean mSubtitleVisible=true;
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private Callbacks mCallbacks;

    private boolean deleteFlags=false;

    public interface Callbacks{
        void onCrimeSelected(Crime crime);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks=(Callbacks)context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list,container,false);
        mCrimeRecyclerView=view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        updateUI();
        return view;
    }
    @Override public void onResume() {
        super.onResume();
        updateUI();
        //在crimefragment页面返回后，因为crimelistfragment会调用onresume方法，因此刷新页面
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks=null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE,mSubtitleVisible);

    }

    public void updateUI() {
        //执行更新list操作
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();
        if(mAdapter==null){
        mAdapter = new CrimeAdapter(crimes);
        mCrimeRecyclerView.setAdapter(mAdapter);}else{
            mAdapter.setCrimes(crimes);
            mAdapter.notifyDataSetChanged();
        }
        updateSubtitle();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list,menu);
        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if(mSubtitleVisible){
            subtitleItem.setTitle(R.string.hide_subtitle);
        }else{
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //工具栏代码

        switch (item.getItemId()){
            case R.id.new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
               // Intent intent = CrimePagerActivity.newIntent(getActivity(),crime.getId());
                //startActivity(intent);
                updateUI();
                mCallbacks.onCrimeSelected(crime);
                return true;
            case R.id.show_subtitle:
                updateSubtitle();
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                return true;
            case R.id.delete:
                //与删除有关的部分
                if(!deleteFlags){
                    deleteFlags=true;
                }else{
                    deleteFlags=false;
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void updateSubtitle(){
        //工具栏上的显示
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();

        String subtitle = getString(R.string.subtitle_format, crimeCount);

        if(!mSubtitleVisible){
            subtitle=null;
        }

        AppCompatActivity activity =(AppCompatActivity)getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);

    }


//下面是适配器代码
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;
        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            LayoutInflater layoutInflater=LayoutInflater.from(getActivity());
            /*View linearLayout=layoutInflater.inflate(R.layout.list_item_crime,viewGroup,false);
            return new CrimeHolder(linearLayout);
            这是我自己写的方式，直接改变viewholder的形参，不按照书上的写也是可以的。这跟以前写的是一样的原理。
        */
            return new CrimeHolder(layoutInflater, viewGroup);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder crimeHolder, int position) {

            Crime crime = mCrimes.get(position);
            crimeHolder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        public void setCrimes(List<Crime> crimes){
            mCrimes = crimes;
        }
    }


    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Crime mCrime;
        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mSolvedImageView;

        /*public CrimeHolder(View il) {//自己写的，把形参改为View il
            super(il);

            mTitleTextView = (TextView) il.findViewById(R.id.crime_title);
            mDateTextView = (TextView) il.findViewById(R.id.crime_date);
        }*/

        //构造函数

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
                super(inflater.inflate(R.layout.list_item_crime, parent, false));
                mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
                mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
                mSolvedImageView = itemView.findViewById(R.id.crime_solved);
                itemView.setOnClickListener(this);
                //不是很理解itemview，难道是默认的一种语法糖？
            }

        public void bind(Crime crime) {
            mCrime = crime;
            mTitleTextView.setText(mCrime.getTitle());
            mDateTextView.setText(mCrime.getDate().toString());
            mSolvedImageView.setVisibility(crime.isSolved()? View.VISIBLE:View.INVISIBLE);
        }

        @Override
        public void onClick(View v) {
            if(deleteFlags){
                Toast.makeText(getActivity(),mCrime.getTitle()+"delete",Toast.LENGTH_SHORT).show();
                CrimeLab crimeLab1=CrimeLab.get(getActivity());
                crimeLab1.deleteCrime(mCrime);
                updateUI();

            }
            Toast.makeText(getActivity(),
                    mCrime.getTitle() + " clicked!", Toast.LENGTH_SHORT)
                    .show();
			//Intent intent = CrimeActivity.newIntent(getActivity(),mCrime.getId());
            //Intent intent = CrimePagerActivity.newIntent(getActivity(),mCrime.getId());

			//startActivity(intent);
            mCallbacks.onCrimeSelected(mCrime);
        }
    }
}
