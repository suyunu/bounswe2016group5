package digest.digestandroid.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import digest.digestandroid.Models.Topic;
import digest.digestandroid.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TopicGeneralFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TopicGeneralFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisteredHomeProfileFragment extends Fragment {
    protected View rootView;
    protected Topic topic = new Topic();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public RegisteredHomeProfileFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TopicGeneralFragment.
     */

    // TODO: Rename and change types and number of parameters
    public static RegisteredHomeProfileFragment newInstance(String param1, String param2) {
        RegisteredHomeProfileFragment fragment = new RegisteredHomeProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        Log.v("onCreate", "onCreate");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_topic_general, container, false);
        // Inflate the layout for this fragment

        Log.v("onCreateView", "onCreateView");

//        setTopicInfo();


        return rootView;

    }


    public void initializeInfo(Topic topic){
        this.topic.setMedia(topic.getMedia());
        this.topic.setBody(topic.getBody());
        this.topic.setOwner(topic.getOwner());
        this.topic.setImage(topic.getImage());
        this.topic.setRating(topic.getRating());
        this.topic.setTags(topic.getTags());
    }

    public void setTopicInfo(){

        TextView header = (TextView) rootView.findViewById(R.id.tTopicHeader);
        header.setText(topic.getHeader());

        TextView desc = (TextView) rootView.findViewById(R.id.tDescription);
        desc.setText(topic.getBody());

        TextView owner = (TextView) rootView.findViewById(R.id.tTopicOwner);
        owner.setText(topic.getOwner());

        TextView rating = (TextView) rootView.findViewById(R.id.tRating);
        rating.setText(String.valueOf(topic.getRating()));




    }


//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction();
    }


}