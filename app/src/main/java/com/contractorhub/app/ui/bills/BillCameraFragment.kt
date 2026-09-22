package com.contractorhub.app.ui.bills

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.contractorhub.app.R
import com.contractorhub.app.databinding.FragmentBillCameraBinding
import com.contractorhub.app.utils.BillImageStorage
import com.contractorhub.app.utils.BillOcrParser
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File

/**
 * PART 22 — Bill Scanner flow: Camera → Capture → OCR → Detected Info → User
 * Review (AddEditBillFragment मध्ये) → Edit → Save.
 * PART 54 — CameraX + on-device OCR, कुठलंही network call नाही (zero-cost, PART 1).
 * OCR result फक्त एक अंदाज आहे — user ला पुढच्या स्क्रीनवर confirm/edit करावंच लागतं.
 */
class BillCameraFragment : Fragment() {

    private var _binding: FragmentBillCameraBinding? = null
    private val binding get() = _binding!!

    private var imageCapture: ImageCapture? = null
    private val siteId: String? get() = arguments?.getString("siteId")
    private val siteName: String? get() = arguments?.getString("siteName")

    private val permissionLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            startCamera()
        } else {
            Toast.makeText(requireContext(), R.string.bill_camera_permission_denied, Toast.LENGTH_LONG).show()
            findNavController().popBackStack()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBillCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }

        binding.buttonCapture.setOnClickListener { capturePhoto() }
    }

    private fun startCamera() {
        val providerFuture = ProcessCameraProvider.getInstance(requireContext())
        providerFuture.addListener({
            val provider = providerFuture.get()

            val preview = androidx.camera.core.Preview.Builder().build().also {
                it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build()

            try {
                provider.unbindAll()
                provider.bindToLifecycle(
                    viewLifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageCapture
                )
            } catch (e: Exception) {
                Toast.makeText(requireContext(), R.string.bill_camera_error, Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun capturePhoto() {
        val capture = imageCapture ?: return
        val photoFile = BillImageStorage.createImageFile(requireContext(), siteName)
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        binding.buttonCapture.isEnabled = false

        capture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    runOcr(photoFile)
                }

                override fun onError(exception: ImageCaptureException) {
                    binding.buttonCapture.isEnabled = true
                    Toast.makeText(requireContext(), R.string.bill_camera_error, Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun runOcr(photoFile: File) {
        binding.progressOcr.isVisible = true
        binding.textOcrStatus.isVisible = true

        val image = InputImage.fromFilePath(requireContext(), Uri.fromFile(photoFile))
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val guessedTotal = BillOcrParser.guessTotalAmount(visionText.text)
                returnResult(photoFile, guessedTotal)
            }
            .addOnFailureListener {
                // OCR अयशस्वी झाला तरी फोटो save झालाय — फक्त amount auto-fill होणार
                // नाही, user ने manually भरायचं (PART 83 — error handling: existing
                // data untouched).
                returnResult(photoFile, null)
            }
    }

    private fun returnResult(photoFile: File, guessedTotal: Long?) {
        setFragmentResult(
            "bill_scan_result",
            bundleOf(
                "imagePath" to photoFile.absolutePath,
                "guessedTotal" to (guessedTotal ?: -1L)
            )
        )
        findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
