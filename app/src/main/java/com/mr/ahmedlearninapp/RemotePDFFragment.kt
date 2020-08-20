package com.mr.ahmedlearninapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import es.voghdev.pdfviewpager.library.RemotePDFViewPager
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter
import es.voghdev.pdfviewpager.library.remote.DownloadFile
import java.lang.NullPointerException

class RemotePDFFragment : Fragment(), DownloadFile.Listener {

    private var remotePDFViewPager: RemotePDFViewPager? = null
    private var placeholder: ViewGroup? = null
    private var mInflater: LayoutInflater? = null
    private var adapter: PDFPagerAdapter? = null

    companion object {
        fun newInstance(filePath: String, fileName: String) = RemotePDFFragment().apply {
            arguments = Bundle().apply {
                putString("filePath", filePath)
                putString("fileName", fileName)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mInflater = inflater
        placeholder = container

        val v = inflater.inflate(R.layout.remote_pdf_fragment, container, false)
        placeholder = v as ViewGroup

        return placeholder
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val fileUrl = arguments?.getString("filePath")
        val fileName = arguments?.getString("fileName")

        remotePDFViewPager = RemotePDFViewPager(context, fileUrl, this)

    }

    override fun onSuccess(url: String, destinationPath: String) {
        // That's the positive case. PDF Download went fine
        adapter = PDFPagerAdapter(context!!, destinationPath)
        remotePDFViewPager?.adapter = adapter
        placeholder?.removeAllViews()
        placeholder?.addView(remotePDFViewPager)
        //   animatedCircleLoadingViewPreviewPdf.stopOk()

    }

    override fun onFailure(e: Exception) {
        // This will be called if download fails
    }

    override fun onProgressUpdate(progress: Int, total: Int) {
        try {
            val dProgress = (progress.toDouble() / total.toDouble()) * 100.0
            // previewProgress.text = "$dProgress"
            // if (animatedCircleLoadingViewPreviewPdf != null)
            //   animatedCircleLoadingViewPreviewPdf?.setPercent(dProgress.toInt())
        } catch (e: NullPointerException) {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (remotePDFViewPager?.adapter as PDFPagerAdapter).close()
        if (remotePDFViewPager != null) {
            //     remotePDFViewPager?.interruptDownloadThread()
            remotePDFViewPager = null
        }

        if (adapter != null) {
            adapter?.close()
            adapter = null
        }

    }

}
