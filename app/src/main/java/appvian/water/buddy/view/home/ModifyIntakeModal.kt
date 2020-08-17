package appvian.water.buddy.view.home

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import appvian.water.buddy.R
import appvian.water.buddy.model.data.Category
import appvian.water.buddy.model.data.Intake
import appvian.water.buddy.view.CategoryRecyclerViewAdapter
import appvian.water.buddy.viewmodel.FavoriteViewModel
import appvian.water.buddy.viewmodel.HomeViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.bottom_sheet_modal.*
import kotlinx.android.synthetic.main.bottom_sheet_modal.view.*


class ModifyIntakeModal(val intake: Intake) : BottomSheetDialogFragment() , TextWatcher{
    private lateinit var homeViewModel: HomeViewModel
    var categoryList = arrayListOf<Category>()
    //ontext changed 결과값
    var result = intake.amount.toString()
    private var selected : Array<Boolean> = Array(3) { i -> false }
    private var selected_idx = 0
    private lateinit var selected_btns : Array<Button>

    private val PAPER_CUP = "120"
    private val TALL_CUP = "350"
    private val TUMBLER_CUP = "600"
    override fun getTheme(): Int = R.style.RoundBottomSheetDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireActivity(),theme)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val v = inflater.inflate(R.layout.bottom_sheet_modal,container,false)

        var typeofDrink = intake.category
        setCategory()
        v.recyclerview.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
            adapter = CategoryRecyclerViewAdapter(
                context,
                categoryList
            ) { category ->
                typeofDrink = category.id
            }
        }
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        v.setButton.setOnClickListener {
            val regex = Regex("[^0-9]")
            val pickedNum = edt_amount.text.toString().replace(regex,"").toInt()
            homeViewModel.modifyCategory(intake.date,typeofDrink)
            homeViewModel.modifyAmount(intake.date,pickedNum)

            onDestroyView()
            onDestroy()
        }
        v.setButton.text = "수정하기"

        v.img_cancel.setOnClickListener{
            onDestroyView()
            onDestroy()
        }
        selected_btns = arrayOf(v.btn_papercup, v.btn_takeout_tall, v.btn_tumbler)
        v.btn_papercup.setOnClickListener {
            btnClick(0)

        }
        v.btn_takeout_tall.setOnClickListener {
            btnClick(1)
        }
        v.btn_tumbler.setOnClickListener {
            btnClick(2)
        }



        v.edt_amount.addTextChangedListener(this)
        v.edt_amount.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus){
                Log.d("TAG","focus")
            }else{
                Log.d("TAG","un focus")
                hideKeyBoard()
            }
        }
        v.edt_amount.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_DONE){
                Log.d("TAG","ime action done")
                hideKeyBoard()
                v.edt_amount.clearFocus()
                true
            }else false
        })
        v.edt_amount.post(Runnable {
            v.edt_amount.isFocusableInTouchMode = true
            v.edt_amount.requestFocus()
            upKeyboard()
        })
        return v
    }
    private fun btnClick(button_pos : Int){
        var idx = 0
        if(!selected[button_pos]){
            for(btn in selected_btns){
                if(idx == button_pos){
                    selected[idx] = true
                    selected_btns[idx].background = resources.getDrawable(R.drawable.btn_bottom_sheet_blue, null)
                    selected_btns[idx].setTextColor(resources.getColor(R.color.white, null))
                    selected_idx = idx
                    when(idx){
                        0 -> {
                            result = PAPER_CUP + "ml"
                        }
                        1 -> {
                            result = TALL_CUP + "ml"
                        }
                        2 -> {
                            result = TUMBLER_CUP + "ml"
                        }
                    }
                    edt_amount.setText(result)
                    edt_amount.setSelection(result.length - 2)
                    setBtnClickable()
                }else{
                    selected[idx] = false
                    selected_btns[idx].background = resources.getDrawable(R.drawable.btn_bottom_sheet, null)
                    selected_btns[idx].setTextColor(resources.getColor(R.color.grey_7, null))
                }
                idx++
            }
        }else{
            edt_amount.text.clear()
            setBtnUnClickable()
            selected[button_pos] = false
            selected_btns[button_pos].background = resources.getDrawable(R.drawable.btn_bottom_sheet, null)
            selected_btns[button_pos].setTextColor(resources.getColor(R.color.grey_7, null))
        }
    }

    private fun setCategory(){
        categoryList.add(Category(0,"물","icon_water"))
        categoryList.add(Category(1,"커피","icon_coffee"))
        categoryList.add(Category(2,"차","icon_tea"))
        categoryList.add(Category(3,"우유","icon_milk"))
        categoryList.add(Category(4,"탄산음료","icon_carbon"))
        categoryList.add(Category(5,"주스","icon_juice"))
        categoryList.add(Category(6,"주류","icon_alcohol"))
        categoryList.add(Category(7,"이온음료","icon_ion"))
        categoryList.add(Category(8,"기타","icon_etc"))

    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
    }

    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        var cur = s.toString()
        if(!cur.equals(result)){//오버플로우 방지
            val regex = Regex("[^0-9]")
            cur = cur.replace(regex, "")
            if(cur.isBlank()){
                edt_amount.text.clear()
                result = ""
                setBtnUnClickable()
            }else {
                result = cur + "ml"
                edt_amount.setText(result)
                edt_amount.setSelection(result.length - 2)
                setBtnClickable()
            }
        }
    }
    private fun setBtnClickable(){
        setButton.setBackgroundColor(resources.getColor(R.color.btnBlueNormal, null))
        setButton.setTextColor(resources.getColor(R.color.white, null))
        setButton.isEnabled = true
    }
    private fun setBtnUnClickable(){
        setButton.setBackgroundColor(resources.getColor(R.color.btnBlueInactive, null))
        setButton.setTextColor(resources.getColor(R.color.grey_1, null))
        setButton.isEnabled = false
    }
    //키보드 올리기
    private fun upKeyboard(){
        var imm = (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        imm.showSoftInput(edt_amount, InputMethodManager.SHOW_FORCED)

    }
    //키보드 숨기기
    private fun hideKeyBoard(){
        var imm = (requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
        imm.hideSoftInputFromWindow(edt_amount.windowToken, 0)
    }

    override fun onDestroyView() {
        hideKeyBoard()
        super.onDestroyView()
    }
}