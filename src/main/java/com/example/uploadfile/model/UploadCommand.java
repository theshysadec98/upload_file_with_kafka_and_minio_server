package com.example.uploadfile.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Builder
@Getter
@Setter
@Entity
@Table(name = "upload_command")
@AllArgsConstructor
@NoArgsConstructor
public class UploadCommand {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bucket")
    private String bucket;

    @Column(name = "source_url")
    private String sourceUrl;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "thumbnail")
    private String thumbnail;
}
