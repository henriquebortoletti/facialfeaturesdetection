package com.example.facial_features_detection;

import com.example.facial_features_detection.databinding.FragmentUserInfoBinding;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;

public class UserInfoFragment extends Fragment {

    private FragmentUserInfoBinding binding;

    private EditText etNome, etNomeResponsavel, etGrauParentesco, etNascimento, etEmail;
    private RadioGroup rgSexo;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Dados");
        binding = FragmentUserInfoBinding.inflate(inflater, container, false);
        View view = inflater.inflate(R.layout.fragment_face_detection, container, false);
        etNome = view.findViewById(R.id.et_name);
        etNomeResponsavel = view.findViewById(R.id.et_nome_responsavel);
        etGrauParentesco = view.findViewById(R.id.et_parentesco);
        etNascimento = view.findViewById(R.id.et_date);
        etEmail = view.findViewById(R.id.et_email);
        rgSexo = view.findViewById(R.id.rg_sexo);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnAvancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(UserInfoFragment.this)
                        .navigate(R.id.action_userInfoFragment_to_faceDetectionFragment3);
            }
        });
    }

    public void registerUser(View view) {
        String name = etNome.getText().toString();
        String responsibleName = etNomeResponsavel.getText().toString();
        String relationship = etGrauParentesco.getText().toString();
        String birth = etNascimento.getText().toString();
        String email = etEmail.getText().toString();
        int sex = rgSexo.getCheckedRadioButtonId();
    }

}