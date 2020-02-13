package com.example.demo_kotlin.Helper

import android.util.Log
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import java.io.IOException
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException

class XMLDOMParser {
    fun getDocument(xml: String?): Document? {
        var document: Document? = null
        val factory =
            DocumentBuilderFactory.newInstance()
        try {
            val db = factory.newDocumentBuilder()
            val `is` = InputSource()
            `is`.characterStream = StringReader(xml)
            `is`.encoding = "UTF-8"
            document = db.parse(`is`)
        } catch (e: ParserConfigurationException) {
            Log.e("Error: ", e.message, e)
            return null
        } catch (e: SAXException) {
            Log.e("Error: ", e.message, e)
            return null
        } catch (e: IOException) {
            Log.e("Error: ", e.message, e)
            return null
        }
        return document
    }

    fun getValue(item: Element, name: String?): String {
        val nodes = item.getElementsByTagName(name)
        return getTextNodeValue(nodes.item(0))
    }

    private fun getTextNodeValue(elem: Node?): String {
        var child: Node?
        if (elem != null) {
            if (elem.hasChildNodes()) {
                child = elem.firstChild
                while (child != null) {
                    if (child.nodeType == Node.TEXT_NODE) {
                        return child.nodeValue
                    }
                    child = child.nextSibling
                }
            }
        }
        return ""
    }
}