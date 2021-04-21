package com.mr.ahmedlearninapp

import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import es.voghdev.pdfviewpager.library.RemotePDFViewPager
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter
import es.voghdev.pdfviewpager.library.remote.DownloadFile
import kotlinx.android.synthetic.main.remote_pdf_fragment.*
import java.lang.NullPointerException

class RemotePDFActivity : AppCompatActivity(), DownloadFile.Listener {

    private var remotePDFViewPager: RemotePDFViewPager? = null
    private var adapter: PDFPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
   //     window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(R.layout.remote_pdf_fragment)

        val fileUrl = intent?.getStringExtra("filePath")
        val lessonName = intent?.getStringExtra(LESSON_NAME)

        title = lessonName
        remotePDFViewPager = RemotePDFViewPager(this, fileUrl, this)

    }

    override fun onSuccess(url: String, destinationPath: String) {
        // That's the positive case. PDF Download went fine
        adapter = PDFPagerAdapter(this, destinationPath)
        remotePDFViewPager?.adapter = adapter
        placeholder?.removeAllViews()
        placeholder?.addView(remotePDFViewPager)
        //   animatedCircleLoadingViewPreviewPdf.stopOk()

    }

    override fun onFailure(e: Exception) {
        // This will be called if download fails
       finish()
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
        try {
            if (remotePDFViewPager != null) {
                (remotePDFViewPager?.adapter as PDFPagerAdapter).close()
                remotePDFViewPager = null
            }

            if (adapter != null) {
                adapter = null
            }
        }catch (e : java.lang.Exception){}


    }

}
