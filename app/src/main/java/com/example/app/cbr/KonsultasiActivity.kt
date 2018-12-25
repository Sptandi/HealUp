package com.example.app.cbr

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Button
import com.example.app.cbr.adapter.ChecklistAdapter
import com.example.app.cbr.model.*
import kotlinx.android.synthetic.main.activity_konsultasi.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.text.SimpleDateFormat
import java.util.*

class KonsultasiActivity : AppCompatActivity() {

    var hasilListKomponen: Double = 0.0
    private var db: AppDatabase? = null
    var userSession: String? = ""
    //var jumlah: Int = 0
    var gejalaCalculation: MutableList<Gejala> = arrayListOf()
    lateinit var rv: RecyclerView
    lateinit var formatDate: String
    lateinit var btCheck: Button
    lateinit var adapter: ChecklistAdapter
    val hasilPerhitungan: ArrayList<Int>? = arrayListOf()
    var gejalas = mutableListOf<Gejala>()

    /*val kasusBaru: List<Gejala> = listOf<Gejala>(
        Gejala(idGejala = 4, namaGejala = "Gejala1", bobot = 10),
        Gejala(idGejala = 5, namaGejala = "Gejala1", bobot = 6),
        Gejala(idGejala = 6, namaGejala = "Gejala1", bobot = 5),
        Gejala(idGejala = 8, namaGejala = "Gejala1", bobot = 2)
    )*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_konsultasi)
        db = AppDatabase.getAppDataBase(this@KonsultasiActivity)

        addSolusi()
        addGejala()
        addKasus()
       // addGejala()

        val nama: String = etNamaAnak.text.toString()
        val c: Date = Calendar.getInstance().time
        val df: SimpleDateFormat = SimpleDateFormat("dd-MMM-yyyy")
        formatDate = df.format(c)


        db?.KomponenGejalaDao()?.add(KomponenGejala(kasusGejala = 1, kasusId = 1, gejalaId = 1))
        db?.KomponenGejalaDao()?.add(KomponenGejala(kasusGejala = 2, kasusId = 1, gejalaId = 3))
        db?.KomponenGejalaDao()?.add(KomponenGejala(kasusGejala = 3, kasusId = 1, gejalaId = 4))
        db?.KomponenGejalaDao()?.add(KomponenGejala(kasusGejala = 4, kasusId = 1, gejalaId = 6))
        db?.KomponenGejalaDao()?.add(KomponenGejala(kasusGejala = 5, kasusId = 2, gejalaId = 2))
        db?.KomponenGejalaDao()?.add(KomponenGejala(kasusGejala = 6, kasusId = 2, gejalaId = 3))
        db?.KomponenGejalaDao()?.add(KomponenGejala(kasusGejala = 7, kasusId = 2, gejalaId = 4))
        db?.KomponenGejalaDao()?.add(KomponenGejala(kasusGejala = 8, kasusId = 3, gejalaId = 3))
        db?.KomponenGejalaDao()?.add(KomponenGejala(kasusGejala = 9, kasusId = 3, gejalaId = 8))
        db?.KomponenGejalaDao()?.add(KomponenGejala(kasusGejala = 10, kasusId = 3, gejalaId = 9))
        //db?.KomponenGejalaDao()?.add(KomponenGejala(kasusId = 3, gejalaId = 6))

        rv = findViewById(R.id.rv)
        btCheck = findViewById(R.id.btCheck)

        //initialize all checkbox with data gejala from db
        for (i in 1..61) {
            val gejala = db?.GejalaDao()?.getAllGejala(i)
            if (gejala != null) {
                gejalas.add(gejala)
            }
        }

        //set to adapter
        adapter = ChecklistAdapter(this, gejalas)
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        //getting checkbox that checked
        btCheck.setOnClickListener() {
            val data = adapter.getItem()
            data.map {
                    if(it.isSelected){
                        gejalaCalculation.add(it)
                }
            }
            getUserSession()
            checkKemiripan()
        }
    }

    fun getUserSession(){
        val sharedPref: SharedPreferences = getSharedPreferences("file", Context.MODE_PRIVATE)
        userSession = sharedPref.getString("key-username", "tidak ada")
    }


    fun checkKemiripan() {
        doAsync {
            val jumlahKasus = db?.KasusDao()?.getCountKasus()
            if (jumlahKasus != null) {
                //mengulang sebanyak kasus
                for (l in 1..jumlahKasus) {
                    var bobotSemua: Double = 0.0
                    var bobotBaru: Double = 0.0
                    val listKasus = db?.KomponenGejalaDao()?.getAllSameId(l)
                    if (listKasus != null) {
                        for( j in listKasus) {
                            for ( i in gejalaCalculation) {
                                if (j.gejalaId == i.idGejala) {
                                    bobotBaru += i.bobot
                                    //Log.e("hasil", bobotBaru.toString())
                                }
                            }
                            val getBobot: Int? = db?.GejalaDao()?.getGejala(j.gejalaId)
                            bobotSemua += getBobot!!
                        }
                    }
                    hasilListKomponen = bobotBaru / bobotSemua
                    //Log.e("bobot", bobotBaru.toString())
                    //Log.e("bobot", bobotSemua.toString())
                    //Log.e("hasil", hasilListKomponen.toString())
                    if (hasilListKomponen >= 0.75) {
                        val hasil: Int? = db?.KasusDao()?.getSolusiById(l)
                        if (hasil != null) {
                            hasilPerhitungan?.add(hasil)
                        }
                    }
                }
                val c = Calendar.getInstance().time
                db?.KonsultasiDao()?.insertKonsultasi(Konsultasi(
                    namaAnak = etNamaAnak.text.toString() , userId = db?.UserDao()?.userIdSession(userSession), tanggal = c))
                val getLast = db?.KonsultasiDao()?.getLastRow()
                for (i in gejalaCalculation) {
                    if (hasilPerhitungan != null) {
                        for (j in hasilPerhitungan) {
                            db?.GejalaSolusiDao()?.insertGejalaSolusi(GejalaSolusi(idGejalaSolusi = getLast, gejalaId = i.idGejala, solusiId = j ))
                        }
                    }
                }
            }
            uiThread {
                val intent = Intent(this@KonsultasiActivity, SolusiActivity::class.java)
                startActivity(intent)
            }
        }
    }

    fun addGejala() {
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Pola asuh ibu dan bapak bertentangan", bobot = 5))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Orang tua bercerai", bobot = 4))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Orang tua sering bertengkar", bobot = 4))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Orang tua terlalu posesif", bobot = 4))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Suka berselisih dengan orang tua", bobot = 4))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Tidak pernah mendengarkan nasihat orang tua", bobot = 2))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Segala yang dia butuhkan selalu dituruti orang tua", bobot = 2))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Orang tua meninggal", bobot = 4))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Orang tua sakit", bobot = 4))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Orang tua berhenti bekerja", bobot = 4))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Orang tua mengalami kesulitan keuangan", bobot = 4))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Merasa tidak nyaman dengan orang tua", bobot = 4))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Orang tua tidak mendukung cita-citanya", bobot = 4))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Anggota keluarga terlalu banyak", bobot = 3))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Menjadi tulang punggung ekonomi keluarga", bobot = 3))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Kesulitan keuangan", bobot = 3))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Suka kabur dari rumah ketika keinginannya tidak dituruti",bobot = 2))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Suka berselisih dengan saudaranya", bobot = 2))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Tidak nyaman/betah tinggal di rumah", bobot = 2))
        db?.GejalaDao()?.insertGejala(Gejala(namaGejala = "Ada anggota keluarga atau orang yang dekatnya meninggal", bobot = 3))
    }

    fun addSolusi() {
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 1, nama = "Orangtua harus selalu disampingnya"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 2, nama = "Peran teman dekat sangat dibutuhka"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 3, nama = "Mencari pekerjaan yang dekat dari rumah agar tidak jauh dari keluarga"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 4, nama = "Butuh perhatian dari orangtua"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 5, nama = "Dijauhkan dari lingkungan yang rusak "))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 6, nama = "Rehabilitasi "))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 7, nama = "Orangtua seharusnya membiarkan apa yang diinginkan anaknya selagi itu hal positif "))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 8, nama = "Keluarga harus membuat suasana yang nyaman"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 9, nama = "Tidak boleh dimanjakan"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 10, nama = "Orangtua harus berdamai"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 11, nama = "Mencari pekerjaan untuk membantu keluarga"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 12, nama = "Lingkungan keluarga harus kondusif sehingga anak bisa belajar dengan baik"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 13, nama = "Mandiri dan harus berpikiran dewasa"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 14, nama = "Sekolah sambil bekerja"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 15, nama = "Mencari pekerjaan "))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 16, nama = "Bersekolah sambil bekerja dan tetap semangat "))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 17, nama = "Didikan orangtua harus diperbaiki karena akan berpengaruh ke lingkungan sekitar"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 18, nama = "Hilangkan sifat manja dan kekanakan pada anak "))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 19, nama = "Berdamai dengan saudara "))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 20, nama = "Kurangi sikap manja dan boros"))
        db?.SolusiDao()?.insertSolusi(Solusi(idSolusi = 21, nama = "Hilangkan trauma anak "))
        /*Solusi(idSolusi = 22, nama = "Pendekatan orangtua"),
        Solusi(idSolusi = 23, nama = "Menerima kondisi yang ada sekarang dan berhenti menyalahkan diri sendiri"),
        Solusi(idSolusi = 24, nama = "Kendalikan amarah dan selalu berpikir positif apa yang diberikan oleh orang lain"),
        Solusi(idSolusi = 25, nama = "Orangtua seharusnya bisa memberikan kebebasan yang bertanggung jawab pada anaknya"),
        Solusi(idSolusi = 26, nama = "Pendekatan psikolog"),
        Solusi(idSolusi = 27, nama = "Berhenti dimanja dan dilatih untuk mandiri "),
        Solusi(idSolusi = 28, nama = "Pendekatan orangtua dan psikolog keluarga"),
        Solusi(idSolusi = 29, nama = "Mengendalikan amarah dan perbanyak mendekatkan diri pada Tuhan"),
        Solusi(idSolusi = 30, nama = "Ibadah yang rajin dan perbanyak kegiatan sosial"),
        Solusi(idSolusi = 31, nama = "Perbanyak kegiatan sosial dan harus move on "),
        Solusi(idSolusi = 32, nama = "Move on dan perbanyak kegiatan positif"),
        Solusi(idSolusi = 33, nama = "Mengutamakan keluarga dan memperbaiki cara belajar"),
        Solusi(idSolusi = 34, nama = "Perbanyak ibadah dan jangan merepotkan orangtua"),
        Solusi(idSolusi = 35, nama = "Optimis menjalani hidup "),
        Solusi(idSolusi = 36, nama = "Membuka diri dan hidup dengan optimis "),
        Solusi(idSolusi = 37, nama = "Pendekatan keluarga dan psikolog"),
        Solusi(idSolusi = 38, nama = "Selalu optimis dan hentikan menyalahkan diri sendiri "),
        Solusi(idSolusi = 39, nama = "Butuh seseorang yang terdekat untuk membuka dirinya agar lebih berani"),
        Solusi(idSolusi = 40, nama = "Perbanyak mendekatkan diri pada Tuhan"),
        Solusi(idSolusi = 41, nama = "Perbanyak motivasi hidup dan ibadah "),
        Solusi(idSolusi = 42, nama = "Percaya pada diri sendiri"),
        Solusi(idSolusi = 43, nama = "Periksa ke psikolog"),
        Solusi(idSolusi = 44, nama = "Move on"),
        Solusi(idSolusi = 45, nama = "Membuka diri dengan lingkungan sekitarnya"),
        Solusi(idSolusi = 46, nama = "Membuka diri dan menerima apa adanya"),
        Solusi(idSolusi = 47, nama = "Kurangi stres berlebih dan kendalikan diri sendiri "),
        Solusi(idSolusi = 48, nama = "Lupakan kenangan buruk dan jangan takut gagal saat belajar"),
        Solusi(idSolusi = 49, nama = "Perbanyak ibadah dan curhat ke orang terdekat "),
        Solusi(idSolusi = 50, nama = "Dibutuhkan motivasi dari orang terdekat")*/
            }

    fun addKasus() {
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 1,  solusiId = 1))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 2,  solusiId = 2))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 3,  solusiId = 3))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 4,  solusiId = 4))

       /* db?.KasusDao()?.insertKasus(Kasus(idKasus = 2, kgId = 2, solusiId = 2))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 2, kgId = 45, solusiId = 2))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 2, kgId = 46, solusiId = 2))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 2, kgId = 49, solusiId = 2))

        db?.KasusDao()?.insertKasus(Kasus(idKasus = 3, kgId = 2, solusiId = 3))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 3, kgId = 14, solusiId = 3))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 3, kgId = 15, solusiId = 3))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 3, kgId = 16, solusiId = 3))

        db?.KasusDao()?.insertKasus(Kasus(idKasus = 4, kgId = 2, solusiId = 4))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 4, kgId = 7, solusiId = 4))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 4, kgId = 22, solusiId = 4))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 4, kgId = 39, solusiId = 4))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 4, kgId = 55, solusiId = 4))

        db?.KasusDao()?.insertKasus(Kasus(idKasus = 5, kgId = 2, solusiId = 5))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 5, kgId = 19, solusiId = 5))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 5, kgId = 37, solusiId = 5))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 5, kgId = 58, solusiId = 5))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 5, kgId = 59, solusiId = 5))

        db?.KasusDao()?.insertKasus(Kasus(idKasus = 6, kgId = 2, solusiId = 6))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 6, kgId = 47, solusiId = 6))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 6, kgId = 57, solusiId = 6))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 6, kgId = 60, solusiId = 6))

        db?.KasusDao()?.insertKasus(Kasus(idKasus = 7, kgId = 4, solusiId = 7))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 7, kgId = 13, solusiId = 7))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 7, kgId = 54, solusiId = 7))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 7, kgId = 59, solusiId = 7))


        db?.KasusDao()?.insertKasus(Kasus(idKasus = 8, kgId = 1, solusiId = 8))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 8, kgId = 6, solusiId = 8))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 8, kgId = 12, solusiId = 8))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 8, kgId = 17, solusiId = 8))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 8, kgId = 29, solusiId = 8))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 8, kgId = 30, solusiId = 8))

        db?.KasusDao()?.insertKasus(Kasus(idKasus = 9, kgId = 3, solusiId = 9))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 9, kgId = 6, solusiId = 9))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 9, kgId = 7, solusiId = 9))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 9, kgId = 17, solusiId = 9))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 9, kgId = 29, solusiId = 9))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 9, kgId = 30, solusiId = 9))

        db?.KasusDao()?.insertKasus(Kasus(idKasus = 10, kgId = 1, solusiId = 10))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 10, kgId = 37, solusiId = 10))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 10, kgId = 13, solusiId = 10))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 10, kgId = 19, solusiId = 10))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 10, kgId = 39, solusiId = 10))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 10, kgId = 47, solusiId = 10))

        db?.KasusDao()?.insertKasus(Kasus(idKasus = 11, kgId = 3, solusiId = 11))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 11, kgId = 10, solusiId = 11))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 11, kgId = 11, solusiId = 11))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 11, kgId = 114, solusiId = 11))

        db?.KasusDao()?.insertKasus(Kasus(idKasus = 12, kgId = 1, solusiId = 12))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 12, kgId = 6, solusiId = 12))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 12, kgId = 12, solusiId = 12))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 12, kgId = 17, solusiId = 12))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 12, kgId = 29, solusiId = 12))
        db?.KasusDao()?.insertKasus(Kasus(idKasus = 12, kgId = 30, solusiId = 12))*/
    }

}

/*

    fun setNameCheckBox() {
        //checkbox_one.text = db?.GejalaDao()?.getGejalaName(1).toString()
        checkbox_two.text = db?.GejalaDao()?.getGejalaName(2).toString()
        checkbox_three.text = db?.GejalaDao()?.getGejalaName(3).toString()
        checkbox_four.text = db?.GejalaDao()?.getGejalaName(4).toString()
    }

    fun onCheckboxClicked(view: View) {
        if (view is CheckBox) {
            val checked: Boolean = view.isChecked
            when (view.id) {
                */

/*

    getUserSession()
    Log.e("DB", userSession.toString())
    val userId = db?.UserDao()?.userIdSession(userSession)
    Log.e("DB", userId.toString())

    setNameCheckBox()
*/


/*
        btCheck.setOnClickListener {
            *//*val nama = etNamaAnak.text.toString()
            val sharedPref = getSharedPreferences("file", Context.MODE_PRIVATE)
            val emailFromPref = sharedPref.getString("key-email", "-")
            val getId: Int? = db?.UserDao()?.userIdSession(emailFromPref)*//**//*

            )*//*
            // db?.KomponenGejalaDao()?.add(listKomponenGejala)
            //  Log.e("DB", db?.KomponenGejalaDao()?.getAll().toString())
            checkKemiripan()
            for (i in 1..gejala.size) {
                Log.e("DB", i.toString())
            }
            for (i in 1..hasilPerhitungan.size) {
                Log.e("DB", i.toString())
            }
        }*/
