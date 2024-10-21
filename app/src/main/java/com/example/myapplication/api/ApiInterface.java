package com.example.myapplication.api;

import android.media.metrics.Event;

import com.example.myapplication.model.BookModel;
import com.example.myapplication.model.ContactModel;
import com.example.myapplication.model.DonationModel;
import com.example.myapplication.model.EventModel;
import com.example.myapplication.model.LoginModel;
import com.example.myapplication.model.MusicModel;
import com.example.myapplication.model.ParticipentModel;
import com.example.myapplication.model.RegisterModel;
import com.example.myapplication.model.SliderImage;

import io.reactivex.rxjava3.core.Observable;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {

    //* Register API *//
    @Multipart
    @POST("registration")
    Observable<RegisterModel> getRegisterData(@Part("player_id")RequestBody player_id,
                                              @Part("name")RequestBody name,
                                              @Part("email")RequestBody email,
                                              @Part("phone")RequestBody phone,
                                              @Part("password")RequestBody password,
                                              @Part("confirmation_password")RequestBody confPassword);

    //* LOGIN AOI *//
    @Multipart
    @POST("login")
    Observable<LoginModel> loginData(@Part("email") RequestBody email,
                                     @Part("password")RequestBody password);

    @POST("event-list")
    Observable<EventModel> getEventModel();

    @POST("book-list")
    Observable<BookModel> getBookModel();

    @POST("music-list")
    Observable<MusicModel> getMusicModel();

    @Multipart
    @POST("add-donation")
    Observable<DonationModel> getDonationData(@Part("clients_id")RequestBody clientId,
                                              @Part("name")RequestBody name,
                                              @Part("phone")RequestBody phone,
                                              @Part("message")RequestBody message,
                                              @Part("donation_type")RequestBody type);

    @POST("active-slider")
    Observable<SliderImage> getImageData();

    @POST("upcoming-events")
    Observable<EventModel> getUpcomingEventModel();

    @Multipart
    @POST("add-participent")
    Observable<ParticipentModel> getAddParticipent(@Part("clients_id")RequestBody clientId,
                                                   @Part("c_name")RequestBody clientName,
                                                   @Part("events_id")RequestBody eventId,
                                                   @Part("e_title")RequestBody eventTitle,
                                                   @Part("kids")RequestBody kinds,
                                                   @Part("adults")RequestBody adults);

    @Multipart
    @POST("add-contact")
    Observable<ContactModel> getAddContact(@Part("username")RequestBody name,
                                               @Part("phone")RequestBody mobile,
                                               @Part("email")RequestBody email,
                                               @Part("message")RequestBody description);
}
